package aodev.blue.rxsandbox.ui.widget.timeline

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.single.SingleResult
import aodev.blue.rxsandbox.model.single.SingleTimeline
import aodev.blue.rxsandbox.ui.utils.basicMeasure
import aodev.blue.rxsandbox.ui.utils.extension.colorCompat
import aodev.blue.rxsandbox.ui.utils.extension.isLtr
import aodev.blue.rxsandbox.utils.clamp
import aodev.blue.rxsandbox.utils.exhaustive
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject


class SingleTimelineView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)

    companion object {
        private val initialTimeline = SingleTimeline<Int>(SingleResult.None())
    }

    // Data
    private var _timeline: SingleTimeline<Int> = initialTimeline
        set(value) {
            field = value
            timelineSubject.onNext(value)
            invalidate()
        }

    /**
     * Exposed timeline for external use.
     * Updating the timeline resets the current gestures.
     */
    var timeline: SingleTimeline<Int>
        set(value) {
            if (_timeline != value) {
                _timeline = value
                resetCurrentGesture()
            }
        }
        get() = _timeline

    private val timelineSubject: Subject<SingleTimeline<Int>> = BehaviorSubject.createDefault(initialTimeline)
    val timelineFlowable: Flowable<SingleTimeline<Int>>
        get() = timelineSubject.toFlowable(BackpressureStrategy.LATEST)

    var readOnly: Boolean = false


    // Gestures
    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var lastTouchX: Float = 0f
    private var isMoving = false


    // Resources
    private val strokeWidth = context.resources.getDimension(R.dimen.timeline_stroke_width)
    private val padding = context.resources.getDimension(R.dimen.timeline_padding)
    private val innerPaddingStart = context.resources.getDimension(R.dimen.timeline_padding_inner_start)
    private val innerPaddingEnd = context.resources.getDimension(R.dimen.timeline_padding_inner_end)
    private val eventSize = context.resources.getDimension(R.dimen.timeline_event_size)
    private val eventTextSize = context.resources.getDimension(R.dimen.timeline_event_text_size)
    private val errorSize = context.resources.getDimension(R.dimen.timeline_error_size)
    private val errorStrokeWidth = context.resources.getDimension(R.dimen.timeline_error_stroke_width)
    private val touchTargetSize = context.resources.getDimension(R.dimen.timeline_touch_target_size)

    private val strokeColor = context.colorCompat(R.color.timeline_stroke_color)
    private val eventFillColor = context.colorCompat(R.color.timeline_event_fill_color)
    private val eventTextColor = context.colorCompat(R.color.timeline_event_text_color)
    private val errorColor = context.colorCompat(R.color.timeline_error_color)

    // Paint
    private val strokePaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        color = strokeColor
        strokeWidth = this@SingleTimelineView.strokeWidth
        style = Paint.Style.STROKE
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

    // Drawing
    private val textBoundsRect = Rect()
    private val lineDrawer = TimelineLineDrawer(context, TimelineViewTypeText.SINGLE)


    //region Measurement

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = (2 * padding + innerPaddingStart + innerPaddingEnd + 3*touchTargetSize).toInt()
        val desiredHeight = (2 * padding + touchTargetSize).toInt()

        val (width, height) = basicMeasure(widthMeasureSpec, heightMeasureSpec, desiredWidth, desiredHeight)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        lineDrawer.isLtr = isLtr
        lineDrawer.onSizeChanged(w, h)
    }

    //endregion

    //region Drawing

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        lineDrawer.draw(canvas)
        drawResult(canvas, _timeline.result)
    }

    private fun drawResult(canvas: Canvas, result: SingleResult<Int>) {
        val centerHeight = height.toFloat() / 2

        when (result) {
            is SingleResult.None -> Unit
            is SingleResult.Success -> {
                val position = resultPosition(result.time)

                canvas.drawCircle(position, centerHeight, eventSize / 2, eventFillPaint)
                canvas.drawCircle(position, centerHeight, eventSize / 2, strokePaint)

                val eventText = result.value.toString()
                eventTextPaint.getTextBounds(eventText, 0, eventText.length, textBoundsRect)
                val textX = position - textBoundsRect.width().toFloat() / 2 - textBoundsRect.left
                val textY = centerHeight + textBoundsRect.height().toFloat() / 2 - textBoundsRect.bottom

                canvas.drawText(eventText, textX, textY, eventTextPaint)
            }
            is SingleResult.Error -> {
                val position = resultPosition(result.time)
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
        }.exhaustive
    }

    //endregion

    //region Result position

    private val availableWidth: Float
        get() = width - 2 * padding - innerPaddingStart - innerPaddingEnd

    private fun resultPosition(time: Float): Float {
        val timeFactor = time / Config.timelineDuration
        return if (isLtr) {
            timeFactor * availableWidth + padding + innerPaddingStart
        } else {
            (1 - timeFactor) * availableWidth + padding + innerPaddingEnd
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

                if (isTouchingResult(x, y)) {
                    isMoving = true
                }

                lastTouchX = x
                activePointerId = ev.getPointerId(0)
            }

            MotionEvent.ACTION_MOVE -> {
                // Find the index of the active pointer and fetch its position
                val pointerIndex = ev.findPointerIndex(activePointerId)

                val x = ev.getX(pointerIndex)

                // Calculate the distance moved
                val dx = if (isLtr) x - lastTouchX else lastTouchX - x
                lastTouchX = x

                val timeDiff = dx / availableWidth * Config.timelineDuration

                if (isMoving) {
                    moveResult(timeDiff)
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

    private fun isTouchingResult(x: Float, y: Float): Boolean {
        val result = _timeline.result
        val boundingBox = when (result) {
            is SingleResult.None -> null
            is SingleResult.Success -> getResultBoundingBox(result.time)
            is SingleResult.Error -> getResultBoundingBox(result.time)
        }

        return boundingBox?.contains(x, y) ?: false
    }

    private fun getResultBoundingBox(eventTime: Float): RectF {
        val centerHeight = height.toFloat() / 2
        val eventPosition = resultPosition(eventTime)
        return RectF(
                eventPosition - touchTargetSize / 2,
                centerHeight - touchTargetSize / 2,
                eventPosition + touchTargetSize / 2,
                centerHeight + touchTargetSize / 2
        )
    }

    private fun moveResult(timeDiff: Float) {
        val result = _timeline.result

        when (result) {
            is SingleResult.None -> Unit
            is SingleResult.Success -> {
                moveResult(timeDiff, result.time) {
                    SingleResult.Success(it, result.value)
                }
            }
            is SingleResult.Error -> {
                moveResult(timeDiff, result.time) {
                    SingleResult.Error(it)
                }
            }
        }.exhaustive
    }

    private fun moveResult(
            timeDiff: Float,
            time: Float,
            makeWithTime: (Float) -> SingleResult<Int>
    ) {

        val newTime = (time + timeDiff).clamp(0f, Config.timelineDuration.toFloat())
        val result = makeWithTime(newTime)

        this._timeline = _timeline.copy(result = result)
    }

    private fun resetCurrentGesture() {
        activePointerId = MotionEvent.INVALID_POINTER_ID
        lastTouchX = 0f
    }

    //endregion
}
