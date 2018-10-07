package aodev.blue.rxsandbox.ui.widget.scroll

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView


/**
 * A vertical scroll view that doesn't intercept events when they're mostly horizontal.
 *
 * From https://stackoverflow.com/questions/2646028/horizontalscrollview-within-scrollview-touch-handling
 */
class VerticalScrollView : ScrollView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    private var xDistance: Float = 0f
    private var yDistance: Float = 0f
    private var lastX: Float = 0f
    private var lastY: Float = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                yDistance = 0f
                xDistance = yDistance
                lastX = ev.x
                lastY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                val curX = ev.x
                val curY = ev.y
                xDistance += Math.abs(curX - lastX)
                yDistance += Math.abs(curY - lastY)
                lastX = curX
                lastY = curY
                if (xDistance > yDistance)
                    return false
            }
        }

        return super.onInterceptTouchEvent(ev)
    }
}