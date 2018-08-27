package aodev.blue.rxsandbox.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
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
import kotlin.properties.Delegates


class TimelineView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)


    // Data
    var timeline by Delegates.observable<Timeline<Int>?>(null) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            invalidate()
        }
    }
    var readOnly: Boolean = false


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
        timeline?.let { timeline ->
            timeline.termination?.let { drawTerminationEvent(canvas, it) }
            drawEvents(canvas, timeline.events)
        }
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

    private fun drawEvents(canvas: Canvas, events: List<Event<Int>>) {
        val centerHeight = height.toFloat() / 2
        events.forEach { event ->
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

    // The ‘active pointer’ is the one currently moving our object.
    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var lastTouchX: Float = 0f

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (readOnly) return false

        val action = ev.actionMasked

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val pointerIndex = ev.actionIndex
                val x = ev.getX(pointerIndex)
                val y = ev.getY(pointerIndex)

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

                // TODO properly handle this event move
                timeline?.let { timeline ->
                    timeline.termination?.let { termination ->
                        val newTime = (termination.time + timeDiff)
                                .clamp(0f, Config.timelineDuration.toFloat())
                        val termEvent = termination.copy(time = newTime)
                        this.timeline = timeline.copy(timeline.events, termination = termEvent)
                    }
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

    //endregion
}