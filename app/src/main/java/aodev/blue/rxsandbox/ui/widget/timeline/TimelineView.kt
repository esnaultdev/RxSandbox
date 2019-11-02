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
import aodev.blue.rxsandbox.ui.widget.model.TimelineConnection
import aodev.blue.rxsandbox.ui.widget.model.TimelineSelection
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.ConnectionDrawer
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.EventDrawer
import aodev.blue.rxsandbox.ui.widget.timeline.drawer.LineDrawer


class TimelineView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
        super(context, attrs, defStyleAttr, defStyleRes)

    // Data
    /**
     * Backing field for [timeline] that doesn't call [onUpdate] when set.
     */
    private var __timeline: Timeline<Any>? = null
        set(value) {
            field = value
            invalidate()
        }

    /**
     * Backing field for [timeline] that calls [onUpdate] when set.
     */
    private var _timeline: Timeline<Any>?
        set(value) {
            __timeline = value
            onUpdate(value)
        }
        get() = __timeline

    /**
     * Exposed timeline for the external user.
     * Updating the timeline resets the current gesture.
     */
    var timeline: Timeline<Any>?
        set(value) {
            val oldValue = _timeline
            __timeline = value
            if (oldValue?.type != value?.type) {
                gestureHandler.resetCurrentGesture()
            }
        }
        get() = _timeline

    /**
     * Function called when an update of the timeline has occurred.
     * This is not called when the timeline is set using the [timeline] property, only when the user
     * has interacted with the timeline elements.
     */
    var onUpdate: (Timeline<Any>?) -> Unit = {}

    /**
     * If set to true, the user cannot interact with the view elements.
     * This should be set to true if a timeline is the result of an operator.
     */
    var readOnly: Boolean = false

    /**
     * The selection state of the timeline.
     * This updates the display of the starting part of the timeline to connect it to the timeline
     * or operator above.
     */
    var selection: TimelineSelection = TimelineSelection.NONE
        set(value) {
            val oldValue = field
            if (oldValue != value) {
                field = value
                invalidate()
            }
        }

    /**
     * Function called when the user has selected this timeline.
     * Its upstream should now be the one displayed.
     */
    var onSelected: () -> Unit = {}

    /**
     * The down connection of the timeline.
     * This updates the display of the ending part of the timeline to connect it to the timeline
     * or operator below.
     */
    var downConnection: TimelineConnection = TimelineConnection.NONE
        set(value) {
            val oldValue = field
            if (oldValue != value) {
                field = value
                invalidate()
            }
        }

    // Resources
    private val totalPaddingStart = run {
        val paddingStart = resources.getDimension(R.dimen.timeline_padding_start)
        val innerPaddingStart = resources.getDimension(R.dimen.timeline_padding_inner_start)
        paddingStart + innerPaddingStart
    }
    private val totalPaddingEnd = run {
        val paddingEnd = resources.getDimension(R.dimen.timeline_padding_end)
        val innerPaddingEnd = resources.getDimension(R.dimen.timeline_padding_inner_end)
        paddingEnd + innerPaddingEnd
    }
    private val paddingVertical = resources.getDimension(R.dimen.timeline_padding_vertical)
    private val touchTargetSize = resources.getDimension(R.dimen.timeline_touch_target_size)

    // Time mapping
    private val timePositionMapper = TimePositionMapper(totalPaddingStart, totalPaddingEnd)

    // Drawing
    private val lineDrawer = LineDrawer(context)
    private val eventDrawer = EventDrawer(context, timePositionMapper)
    private val connectionDrawer = ConnectionDrawer(context)

    // Gestures
    private var gestureHandler = GestureHandler(context, timePositionMapper, this::_timeline)

    //region Measurement

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = (totalPaddingStart + totalPaddingEnd + 3 * touchTargetSize).toInt()
        val desiredHeight = (2 * paddingVertical + touchTargetSize).toInt()

        val (width, height) = basicMeasure(widthMeasureSpec, heightMeasureSpec, desiredWidth, desiredHeight)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        timePositionMapper.isLtr = isLtr
        timePositionMapper.width = w

        lineDrawer.isLtr = isLtr
        lineDrawer.onSizeChanged(w, h)

        connectionDrawer.isLtr = isLtr

        val centerHeight = h.toFloat() / 2
        eventDrawer.centerHeight = centerHeight
        gestureHandler.centerHeight = centerHeight
    }

    //endregion

    //region Drawing

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        lineDrawer.draw(canvas, timeline?.type)
        connectionDrawer.draw(canvas, downConnection)
        eventDrawer.draw(canvas, isLtr, timeline)
    }

    //endregion

    //region Gestures

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (readOnly) return false

        return gestureHandler.onTouchEvent(ev)
    }

    //endregion
}
