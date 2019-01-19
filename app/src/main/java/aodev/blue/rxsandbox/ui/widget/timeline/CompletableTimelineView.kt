package aodev.blue.rxsandbox.ui.widget.timeline

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.CompletableT
import aodev.blue.rxsandbox.ui.utils.basicMeasure
import aodev.blue.rxsandbox.ui.utils.extension.isLtr
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.CompleteEventDrawer
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.ErrorEventDrawer
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.TimelineLineDrawer
import aodev.blue.rxsandbox.utils.exhaustive


class CompletableTimelineView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)

    companion object {
        private val initialTimeline = CompletableT(CompletableT.Result.None)
    }

    // Data
    private var _timeline: CompletableT = initialTimeline
        set(value) {
            field = value
            onUpdate(value)
            invalidate()
        }

    /**
     * Exposed timeline for external use.
     * Updating the timeline resets the current gestures.
     */
    var timeline: CompletableT
        set(value) {
            if (_timeline != value) {
                _timeline = value
                gestureHelper.resetCurrentGesture()
            }
        }
        get() = _timeline

    var onUpdate: (CompletableT) -> Unit = {}

    var readOnly: Boolean = false

    // Resources
    private val padding = context.resources.getDimension(R.dimen.timeline_padding)
    private val innerPaddingStart = context.resources.getDimension(R.dimen.timeline_padding_inner_start)
    private val innerPaddingEnd = context.resources.getDimension(R.dimen.timeline_padding_inner_end)
    private val touchTargetSize = context.resources.getDimension(R.dimen.timeline_touch_target_size)

    // Drawing
    private val lineDrawer = TimelineLineDrawer(context, TimelineViewTypeText.COMPLETABLE)
    private val completeEventDrawer = CompleteEventDrawer(context)
    private val errorEventDrawer = ErrorEventDrawer(context)

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

    private fun drawResult(canvas: Canvas, result: CompletableT.Result) {
        val centerHeight = height.toFloat() / 2

        when (result) {
            is CompletableT.Result.None -> Unit
            is CompletableT.Result.Complete -> {
                val position = timePositionMapper.position(result.time)
                completeEventDrawer.draw(canvas, position, centerHeight)
            }
            is CompletableT.Result.Error -> {
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
            is CompletableT.Result.None -> false
            is CompletableT.Result.Complete -> isTouchingResultWithTime(x, result.time)
            is CompletableT.Result.Error -> isTouchingResultWithTime(x, result.time)
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
            is CompletableT.Result.None -> Unit
            is CompletableT.Result.Complete -> {
                val newResult = CompletableT.Result.Complete(newTime)
                this._timeline = _timeline.copy(result = newResult)
            }
            is CompletableT.Result.Error -> {
                val newResult = CompletableT.Result.Error(newTime)
                this._timeline = _timeline.copy(result = newResult)
            }
        }.exhaustive
    }

    //endregion
}