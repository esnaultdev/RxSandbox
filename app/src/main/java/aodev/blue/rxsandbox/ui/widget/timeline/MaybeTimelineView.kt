package aodev.blue.rxsandbox.ui.widget.timeline

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.maybe.MaybeResult
import aodev.blue.rxsandbox.model.maybe.MaybeTimeline
import aodev.blue.rxsandbox.ui.utils.basicMeasure
import aodev.blue.rxsandbox.ui.utils.extension.isLtr
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.CompleteEventDrawer
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.ErrorEventDrawer
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.TimelineLineDrawer
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.ValueEventDrawer
import aodev.blue.rxsandbox.utils.clamp
import aodev.blue.rxsandbox.utils.exhaustive
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject


class MaybeTimelineView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)

    companion object {
        private val initialTimeline = MaybeTimeline<Int>(MaybeResult.None())
    }

    // Data
    private var _timeline: MaybeTimeline<Int> = initialTimeline
        set(value) {
            field = value
            timelineSubject.onNext(value)
            invalidate()
        }

    /**
     * Exposed timeline for external use.
     * Updating the timeline resets the current gestures.
     */
    var timeline: MaybeTimeline<Int>
        set(value) {
            if (_timeline != value) {
                _timeline = value
                resetCurrentGesture()
            }
        }
        get() = _timeline

    private val timelineSubject: Subject<MaybeTimeline<Int>> = BehaviorSubject.createDefault(initialTimeline)
    val timelineFlowable: Flowable<MaybeTimeline<Int>>
        get() = timelineSubject.toFlowable(BackpressureStrategy.LATEST)

    var readOnly: Boolean = false


    // Gestures
    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var lastTouchX: Float = 0f
    private var isMoving = false


    // Resources
    private val padding = context.resources.getDimension(R.dimen.timeline_padding)
    private val innerPaddingStart = context.resources.getDimension(R.dimen.timeline_padding_inner_start)
    private val innerPaddingEnd = context.resources.getDimension(R.dimen.timeline_padding_inner_end)
    private val touchTargetSize = context.resources.getDimension(R.dimen.timeline_touch_target_size)

    // Drawing
    private val lineDrawer = TimelineLineDrawer(context, TimelineViewTypeText.MAYBE)
    private val completeEventDrawer = CompleteEventDrawer(context)
    private val errorEventDrawer = ErrorEventDrawer(context)
    private val valueEventDrawer = ValueEventDrawer(context)


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

    private fun drawResult(canvas: Canvas, result: MaybeResult<Int>) {
        val centerHeight = height.toFloat() / 2

        when (result) {
            is MaybeResult.None -> Unit
            is MaybeResult.Success -> {
                val position = resultPosition(result.time)
                valueEventDrawer.draw(canvas, position, centerHeight, result.value)
            }
            is MaybeResult.Complete -> {
                val position = resultPosition(result.time)
                completeEventDrawer.draw(canvas, position, centerHeight)
            }
            is MaybeResult.Error -> {
                val position = resultPosition(result.time)
                errorEventDrawer.draw(canvas, position, centerHeight)
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
            is MaybeResult.None -> null
            is MaybeResult.Success -> getResultBoundingBox(result.time)
            is MaybeResult.Complete -> getResultBoundingBox(result.time)
            is MaybeResult.Error -> getResultBoundingBox(result.time)
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
            is MaybeResult.None -> Unit
            is MaybeResult.Success -> {
                moveResult(timeDiff, result.time) {
                    MaybeResult.Success(it, result.value)
                }
            }
            is MaybeResult.Complete -> {
                moveResult(timeDiff, result.time) {
                    MaybeResult.Complete(it)
                }
            }
            is MaybeResult.Error -> {
                moveResult(timeDiff, result.time) {
                    MaybeResult.Error(it)
                }
            }
        }.exhaustive
    }

    private fun moveResult(
            timeDiff: Float,
            time: Float,
            makeWithTime: (Float) -> MaybeResult<Int>
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
