package aodev.blue.rxsandbox.ui.widget.timeline.drawer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.ui.utils.extension.colorCompat


/**
 * Draw an error event of a timeline.
 */
class ErrorEventDrawer(context: Context) {

    private val errorSize = context.resources.getDimension(R.dimen.timeline_error_size)
    private val errorStrokeWidth = context.resources.getDimension(R.dimen.timeline_error_stroke_width)
    private val errorColor = context.colorCompat(R.color.timeline_error_color)

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
     */
    fun draw(canvas: Canvas, x: Float, y: Float) {
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