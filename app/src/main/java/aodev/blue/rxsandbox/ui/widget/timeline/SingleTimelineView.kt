package aodev.blue.rxsandbox.ui.widget.timeline

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.ui.utils.basicMeasure
import aodev.blue.rxsandbox.ui.utils.extension.isLtr
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.ErrorEventDrawer
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.TimelineLineDrawer
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.ValueEventDrawer
import aodev.blue.rxsandbox.utils.exhaustive
import io.reactivex.Observable
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
        private val initialTimeline = SingleT<Int>(SingleT.Result.None())
    }

    // Data
    private var _timeline: SingleT<Int> = initialTimeline
        set(value) {
            field = value
            timelineSubject.onNext(value)
            invalidate()
        }

    /**
     * Exposed timeline for external use.
     * Updating the timeline resets the current gestures.
     */
    var timeline: SingleT<Int>
        set(value) {
            if (_timeline != value) {
                _timeline = value
                gestureHelper.resetCurrentGesture()
            }
        }
        get() = _timeline

    private val timelineSubject: Subject<SingleT<Int>> = BehaviorSubject.createDefault(initialTimeline)
    val timelineObservable: Observable<SingleT<Int>>
        get() = timelineSubject.hide()

    var readOnly: Boolean = false

    // Resources
    private val padding = context.resources.getDimension(R.dimen.timeline_padding)
    private val innerPaddingStart = context.resources.getDimension(R.dimen.timeline_padding_inner_start)
    private val innerPaddingEnd = context.resources.getDimension(R.dimen.timeline_padding_inner_end)
    private val touchTargetSize = context.resources.getDimension(R.dimen.timeline_touch_target_size)

    // Drawing
    private val lineDrawer = TimelineLineDrawer(context, TimelineViewTypeText.SINGLE)
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

    private fun drawResult(canvas: Canvas, result: SingleT.Result<Int>) {
        val centerHeight = height.toFloat() / 2

        when (result) {
            is SingleT.Result.None -> Unit
            is SingleT.Result.Success -> {
                val position = timePositionMapper.position(result.time)
                valueEventDrawer.draw(canvas, position, centerHeight, result.value)
            }
            is SingleT.Result.Error -> {
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
            is SingleT.Result.None -> false
            is SingleT.Result.Success -> isTouchingResultWithTime(x, result.time)
            is SingleT.Result.Error -> isTouchingResultWithTime(x, result.time)
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
            is SingleT.Result.None -> Unit
            is SingleT.Result.Success -> {
                val newResult = SingleT.Result.Success(newTime, result.value)
                this._timeline = _timeline.copy(result = newResult)
            }
            is SingleT.Result.Error -> {
                val newResult = SingleT.Result.Error<Int>(newTime)
                this._timeline = _timeline.copy(result = newResult)
            }
        }.exhaustive
    }

    //endregion
}
