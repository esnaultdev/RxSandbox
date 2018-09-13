package aodev.blue.rxsandbox.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.Event
import aodev.blue.rxsandbox.model.Termination
import aodev.blue.rxsandbox.model.TerminationEvent
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.ui.utils.extension.colorCompat
import aodev.blue.rxsandbox.ui.utils.extension.isLtr
import aodev.blue.rxsandbox.utils.clamp
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject


class TimelineView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)

    companion object {
        private val initialTimeline = Timeline<Int>(emptySet(), null)

        private const val EVENT_INDEX_NONE = -2
        private const val EVENT_INDEX_TERMINATION = -1
    }

    // Data
    private var _timeline: Timeline<Int> = initialTimeline
        set(value) {
            field = value
            timelineSubject.onNext(value)
            invalidate()
        }

    /**
     * Exposed timeline for external use.
     * Updating the timeline resets the current gestures.
     */
    var timeline: Timeline<Int>
        set(value) {
            if (_timeline != value) {
                _timeline = value
                resetCurrentGesture()
            }
        }
        get() = _timeline

    private val timelineSubject: Subject<Timeline<Int>> = BehaviorSubject.createDefault(initialTimeline)
    val timelineFlowable: Flowable<Timeline<Int>>
        get() = timelineSubject.toFlowable(BackpressureStrategy.LATEST)

    var readOnly: Boolean = false


    // Gestures
    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var lastTouchX: Float = 0f
    private var movingEventIndex = EVENT_INDEX_NONE
    private var eventsToMove: MutableList<Event<Int>> = mutableListOf()


    // Resources
    private val strokeWidth = context.resources.getDimension(R.dimen.timeline_stroke_width)
    private val padding = context.resources.getDimension(R.dimen.timeline_padding)
    private val innerPadding = context.resources.getDimension(R.dimen.timeline_padding_inner)
    private val eventSize = context.resources.getDimension(R.dimen.timeline_event_size)
    private val eventTextSize = context.resources.getDimension(R.dimen.timeline_event_text_size)
    private val completeHeight = context.resources.getDimension(R.dimen.timeline_complete_height)
    private val errorSize = context.resources.getDimension(R.dimen.timeline_error_size)
    private val errorStrokeWidth = context.resources.getDimension(R.dimen.timeline_error_stroke_width)
    private val arrowWidth = context.resources.getDimension(R.dimen.timeline_arrow_width)
    private val arrowHeight = context.resources.getDimension(R.dimen.timeline_arrow_height)
    private val touchTargetSize = context.resources.getDimension(R.dimen.timeline_touch_target_size)

    private val strokeColor = context.colorCompat(R.color.timeline_stroke_color)
    private val eventFillColor = context.colorCompat(R.color.timeline_event_fill_color)
    private val eventTextColor = context.colorCompat(R.color.timeline_event_text_color)
    private val errorColor = context.colorCompat(R.color.timeline_error_color)


    // Paint
    private val strokePaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        color = strokeColor
        strokeWidth = this@TimelineView.strokeWidth
        style = Paint.Style.STROKE
    }
    private val arrowPaint = Paint().apply {
        color = strokeColor
        style = Paint.Style.FILL_AND_STROKE
    }
    private val eventFillPaint = Paint().apply {
        color = eventFillColor
        style = Paint.Style.FILL
    }
    private val eventTextPaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        color = eventTextColor
        textSize = eventTextSize
    }
    private val errorPaint = Paint().apply {
        color = errorColor
        strokeWidth = errorStrokeWidth
        style = Paint.Style.STROKE
    }

    // Draw
    private val arrowPath = Path()
    private val textBoundsRect = Rect()


    //region Measurement

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = (2 * padding + 2 * innerPadding + 10 * eventSize).toInt()
        val desiredHeight = (2 * padding + completeHeight).toInt()

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            View.MeasureSpec.EXACTLY -> widthSize
            View.MeasureSpec.AT_MOST -> minOf(desiredWidth, widthSize)
            View.MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> throw IllegalArgumentException("Illegal measure spec")
        }

        val height = when (heightMode) {
            View.MeasureSpec.EXACTLY -> heightSize
            View.MeasureSpec.AT_MOST -> minOf(desiredHeight, heightSize)
            View.MeasureSpec.UNSPECIFIED -> desiredHeight
            else -> throw IllegalArgumentException("Illegal measure spec")
        }

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        computeArrowPath(w, h)
    }

    private fun computeArrowPath(width: Int, height: Int) {
        arrowPath.run {
            // Starting from the pointy end
            if (isLtr) {
                moveTo(width - padding, height.toFloat() / 2)
                rLineTo(-arrowWidth, -arrowHeight / 2)
                rLineTo(0f, arrowHeight)
                rLineTo(arrowWidth, -arrowHeight / 2)
                close()
            } else {
                moveTo(padding, height.toFloat() / 2)
                rLineTo(arrowWidth, -arrowHeight / 2)
                rLineTo(0f, arrowHeight)
                rLineTo(-arrowWidth, -arrowHeight / 2)
                close()
            }
        }
    }

    //endregion

    //region Drawing

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawLine(canvas)

        _timeline.termination?.let { drawTerminationEvent(canvas, it) }
        drawEvents(canvas, _timeline.sortedEvents)
    }

    private fun drawLine(canvas: Canvas) {
        val centerHeight = height.toFloat() / 2

        canvas.drawLine(padding, centerHeight, width - padding - arrowWidth, centerHeight, strokePaint)
        canvas.drawPath(arrowPath, arrowPaint)
    }

    private fun drawTerminationEvent(canvas: Canvas, event: TerminationEvent) {
        val centerHeight = height.toFloat() / 2
        val position = eventPosition(event.time)

        when (event.value) {
            is Termination.Complete -> {
                canvas.drawLine(
                        position,
                        centerHeight - completeHeight / 2,
                        position,
                        centerHeight + completeHeight / 2,
                        strokePaint
                )
            }
            is Termination.Error -> {
                canvas.drawLine(
                        position - errorSize / 2,
                        centerHeight - errorSize / 2,
                        position + errorSize / 2,
                        centerHeight + errorSize / 2,
                        errorPaint
                )
                canvas.drawLine(
                        position - errorSize / 2,
                        centerHeight + errorSize / 2,
                        position + errorSize / 2,
                        centerHeight - errorSize / 2,
                        errorPaint
                )
            }
        }
    }

    private fun drawEvents(canvas: Canvas, sortedEvents: List<Event<Int>>) {
        val centerHeight = height.toFloat() / 2
        sortedEvents.reversed().forEach { event ->
            val position = eventPosition(event.time)

            canvas.drawCircle(position, centerHeight, eventSize / 2, eventFillPaint)
            canvas.drawCircle(position, centerHeight, eventSize / 2, strokePaint)

            val eventText = event.value.toString()
            eventTextPaint.getTextBounds(eventText, 0, eventText.length, textBoundsRect)
            val textX = position - textBoundsRect.width().toFloat() / 2 - textBoundsRect.left
            val textY = centerHeight + textBoundsRect.height().toFloat() / 2 - textBoundsRect.bottom

            canvas.drawText(event.value.toString(), textX, textY, eventTextPaint)
        }
    }

    //endregion

    //region Event position

    private fun eventPosition(time: Float): Float {
        val timeFactor = time / Config.timelineDuration
        val widthForEvents = width - 2 * padding - 2 * innerPadding
        val startPadding = padding + innerPadding
        return if (isLtr) {
            timeFactor * widthForEvents + startPadding
        } else {
            (1 - timeFactor) * widthForEvents + startPadding
        }
    }

    //endregion

    //region Gestures

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (readOnly) return false

        val action = ev.actionMasked

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val pointerIndex = ev.actionIndex
                val x = ev.getX(pointerIndex)
                val y = ev.getY(pointerIndex)

                eventsToMove = _timeline.sortedEvents.toMutableList()
                movingEventIndex = getEventIndexForPosition(x, y)

                lastTouchX = x
                activePointerId = ev.getPointerId(0)
            }

            MotionEvent.ACTION_MOVE -> {
                // Find the index of the active pointer and fetch its position
                val pointerIndex = ev.findPointerIndex(activePointerId)

                val x = ev.getX(pointerIndex)

                // Calculate the distance moved
                val dx = x - lastTouchX
                lastTouchX = x

                val widthForEvents = width - 2 * padding - 2 * innerPadding
                val timeDiff = dx / widthForEvents * Config.timelineDuration

                val movingEventIndex = movingEventIndex
                when (movingEventIndex) {
                    EVENT_INDEX_NONE -> Unit
                    EVENT_INDEX_TERMINATION -> moveTerminationEvent(timeDiff)
                    else -> moveEvent(movingEventIndex, timeDiff)
                }
            }

            MotionEvent.ACTION_UP -> {
                activePointerId = MotionEvent.INVALID_POINTER_ID
            }

            MotionEvent.ACTION_CANCEL -> {
                activePointerId = MotionEvent.INVALID_POINTER_ID
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = ev.actionIndex
                val pointerId = ev.getPointerId(pointerIndex)

                if (pointerId == activePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    lastTouchX = ev.getX(newPointerIndex)
                    activePointerId = ev.getPointerId(newPointerIndex)
                }
            }
        }
        return true
    }

    private fun getEventIndexForPosition(x: Float, y: Float): Int {
        val boundingBoxes = eventsToMove.map { getEventBoundingBox(it.time) }.toMutableList()
        val terminationBoundingBox = _timeline.termination?.let { getEventBoundingBox(it.time) }
        if (terminationBoundingBox != null) {
            boundingBoxes.add(terminationBoundingBox)
        }

        return boundingBoxes.mapIndexed { index, boundingBox -> index to boundingBox }
                .filter { it.second.contains(x, y) }
                .sortedBy { Math.abs(it.second.centerX() - x) }
                .firstOrNull()
                ?.first
                ?.let { if (it == _timeline.events.size) EVENT_INDEX_TERMINATION else it }
                ?: EVENT_INDEX_NONE
    }

    private fun getEventBoundingBox(eventTime: Float): RectF {
        val centerHeight = height.toFloat() / 2
        val eventPosition = eventPosition(eventTime)
        return RectF(
                eventPosition - touchTargetSize / 2,
                centerHeight - touchTargetSize / 2,
                eventPosition + touchTargetSize / 2,
                centerHeight + touchTargetSize / 2
        )
    }

    private fun moveTerminationEvent(timeDiff: Float) {
        _timeline.termination?.let { termination ->
            val newTime = (termination.time + timeDiff)
                    .clamp(0f, Config.timelineDuration.toFloat())
            val termEvent = termination.moveTo(newTime)

            val events = _timeline.events.map {
                if (it.time > newTime) {
                    it.moveTo(newTime)
                } else {
                    it
                }
            }

            this._timeline = _timeline.copy(
                    events = events.toSet(),
                    termination = termEvent
            )
        }
    }

    private fun moveEvent(eventIndex: Int, timeDiff: Float) {
        val oldEvent = eventsToMove.getOrNull(eventIndex) ?: return
        val newTime = (oldEvent.time + timeDiff).clamp(0f, Config.timelineDuration.toFloat())

        eventsToMove[eventIndex] = oldEvent.moveTo(newTime)

        val termEvent = _timeline.termination?.let {
            if (it.time < newTime) {
                it.moveTo(newTime)
            } else {
                it
            }
        }

        _timeline = _timeline.copy(
                events = eventsToMove.toSet(),
                termination = termEvent
        )
    }

    private fun resetCurrentGesture() {
        activePointerId = MotionEvent.INVALID_POINTER_ID
        lastTouchX = 0f
        movingEventIndex = EVENT_INDEX_NONE
        eventsToMove = mutableListOf()
    }

    //endregion
}