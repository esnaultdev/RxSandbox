package aodev.blue.rxsandbox.ui.widget.timeline

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.maybe.MaybeResult
import aodev.blue.rxsandbox.model.maybe.MaybeTimeline
import aodev.blue.rxsandbox.ui.utils.basicMeasure
import aodev.blue.rxsandbox.ui.utils.extension.isLtr
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.CompleteEventDrawer
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.ErrorEventDrawer
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.TimelineLineDrawer
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.ValueEventDrawer
import aodev.blue.rxsandbox.utils.exhaustive
import io.reactivex.Observable
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
                gestureHelper.resetCurrentGesture()
            }
        }
        get() = _timeline

    private val timelineSubject: Subject<MaybeTimeline<Int>> = BehaviorSubject.createDefault(initialTimeline)
    val timelineObservable: Observable<MaybeTimeline<Int>>
        get() = timelineSubject.hide()

    var readOnly: Boolean = false

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

    // Gestures
    private val timePositionMapper = TimePositionMapper(padding, innerPaddingStart, innerPaddingEnd)
    private val gestureHelper = GestureHelper(timePositionMapper, this::isTouchingResult, this::moveResult)

    //region Measurement

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = (2 * padding + innerPaddingStart + innerPaddingEnd + 3*touchTargetSize).toInt()
        val desiredHeight = (2 * padding + touchTargetSize).toInt()

        val (width, height) = basicMeasure(widthMeasureSpec, heightMeasureSpec, desiredWidth, desiredHeight)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        timePositionMapper.isLtr = isLtr
        timePositionMapper.width = w

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
                val position = timePositionMapper.position(result.time)
                valueEventDrawer.draw(canvas, position, centerHeight, result.value)
            }
            is MaybeResult.Complete -> {
                val position = timePositionMapper.position(result.time)
                completeEventDrawer.draw(canvas, position, centerHeight)
            }
            is MaybeResult.Error -> {
                val position = timePositionMapper.position(result.time)
                errorEventDrawer.draw(canvas, position, centerHeight)
            }
        }.exhaustive
    }

    //endregion

    //region Gestures

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (readOnly) return false

        gestureHelper.onTouchEvent(ev)
        return true
    }

    private fun isTouchingResult(x: Float, y: Float): Boolean {
        val centerHeight = height.toFloat() / 2
        val halfTargetSize = touchTargetSize / 2
        if (y < centerHeight - halfTargetSize || y > centerHeight + halfTargetSize) {
            return false
        }

        val result = _timeline.result
        return when (result) {
            is MaybeResult.None -> false
            is MaybeResult.Complete -> isTouchingResultWithTime(x, result.time)
            is MaybeResult.Success -> isTouchingResultWithTime(x, result.time)
            is MaybeResult.Error -> isTouchingResultWithTime(x, result.time)
        }
    }

    private fun isTouchingResultWithTime(x: Float, eventTime: Float): Boolean {
        val eventPosition = timePositionMapper.position(eventTime)
        val halfTargetSize = touchTargetSize / 2
        return x >= eventPosition - halfTargetSize && x <= eventPosition + halfTargetSize
    }

    private fun moveResult(newTime: Float) {
        val result = _timeline.result

        when (result) {
            is MaybeResult.None -> Unit
            is MaybeResult.Complete -> {
                val newResult = MaybeResult.Complete<Int>(newTime)
                this._timeline = _timeline.copy(result = newResult)
            }
            is MaybeResult.Success -> {
                val newResult = MaybeResult.Success(newTime, result.value)
                this._timeline = _timeline.copy(result = newResult)
            }
            is MaybeResult.Error -> {
                val newResult = MaybeResult.Error<Int>(newTime)
                this._timeline = _timeline.copy(result = newResult)
            }
        }.exhaustive
    }

    //endregion
}
