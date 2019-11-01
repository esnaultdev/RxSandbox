package aodev.blue.rxsandbox.ui.widget.timeline.drawer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.ui.utils.extension.getColorCompat


/**
 * Draw an error event of a timeline.
 */
class ErrorEventDrawer(context: Context) {

    private val errorSizeMin = context.resources.getDimension(R.dimen.timeline_error_size_min)
    private val errorSizeMax = context.resources.getDimension(R.dimen.timeline_error_size_max)
    private val minEventDiff: Float by lazy {
        val valueEventSize = context.resources.getDimension(R.dimen.timeline_event_size)
        errorSizeMin / 2 + valueEventSize / 2
    }
    private val errorStrokeWidth = context.resources.getDimension(R.dimen.timeline_error_stroke_width)
    private val errorColor = context.getColorCompat(R.color.timeline_error_color)

    private val errorPaint = Paint().apply {
        color = errorColor
        strokeWidth = errorStrokeWidth
        style = Paint.Style.STROKE
    }

    /**
     * Draw the error event on the canvas
     * @param canvas The canvas to draw on
     * @param x The x position of the error event
     * @param y The y position of the center of the error event
     * @param previousX The x position of the center of the previous event, if any
     */
    fun draw(canvas: Canvas, x: Float, y: Float, previousX: Float?) {
        val errorSize = if (previousX == null) {
            errorSizeMin
        } else {
            val diffX = x - previousX
            if (diffX  > minEventDiff) {
                errorSizeMin
            } else {
                val t = diffX / minEventDiff
                // Use an easing since the edge we're trying to surpass
                // is the side of a circle that goes up quickly
                val t2 = t * t
                val factor = t2 * t2
                (1 - factor) * errorSizeMax + factor * errorSizeMin
            }
        }

        canvas.drawLine(
                x - errorSize / 2,
                y - errorSize / 2,
                x + errorSize / 2,
                y + errorSize / 2,
                errorPaint
        )
        canvas.drawLine(
                x - errorSize / 2,
                y + errorSize / 2,
                x + errorSize / 2,
                y - errorSize / 2,
                errorPaint
        )
    }
}
