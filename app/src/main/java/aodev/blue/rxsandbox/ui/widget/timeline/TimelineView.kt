package aodev.blue.rxsandbox.ui.widget.timeline

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.ui.utils.basicMeasure
import aodev.blue.rxsandbox.ui.utils.extension.isLtr
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.EventDrawer
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.TimelineLineDrawer


class TimelineView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
        super(context, attrs, defStyleAttr, defStyleRes)

    // Data
    private var _timeline: Timeline<Any>? = null
        set(value) {
            field = value
            onUpdate(value)
            invalidate()
        }

    /**
     * Exposed timeline for the external user.
     * Updating the timeline resets the current gesture.
     */
    var timeline: Timeline<Any>?
        set(value) {
            val oldValue = _timeline
            _timeline = value
            if (oldValue?.type != value?.type) {
                gestureHandler.resetCurrentGesture()
            }
        }
        get() = _timeline

    var onUpdate: (Timeline<Any>?) -> Unit = {}

    var readOnly: Boolean = false

    // Resources
    private val padding = context.resources.getDimension(R.dimen.timeline_padding)
    private val innerPaddingStart = context.resources.getDimension(R.dimen.timeline_padding_inner_start)
    private val innerPaddingEnd = context.resources.getDimension(R.dimen.timeline_padding_inner_end)
    private val touchTargetSize = context.resources.getDimension(R.dimen.timeline_touch_target_size)

    // Time mapping
    private val timePositionMapper = TimePositionMapper(padding, innerPaddingStart, innerPaddingEnd)

    // Drawing
    private val lineDrawer = TimelineLineDrawer(context)
    private val eventDrawer = EventDrawer(context, timePositionMapper)

    // Gestures
    private var gestureHandler = GestureHandler(context, timePositionMapper, this::_timeline)

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

        val centerHeight = h.toFloat() / 2
        eventDrawer.centerHeight = centerHeight
        gestureHandler.centerHeight = centerHeight
    }

    //endregion

    //region Drawing

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        lineDrawer.draw(canvas, timeline?.type)
        eventDrawer.draw(canvas, timeline)
    }

    //endregion

    //region Gestures

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (readOnly) return false

        return gestureHandler.onTouchEvent(ev)
    }

    //endregion
}
