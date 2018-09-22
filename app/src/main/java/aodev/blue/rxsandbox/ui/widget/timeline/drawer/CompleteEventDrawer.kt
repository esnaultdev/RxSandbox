package aodev.blue.rxsandbox.ui.widget.timeline.drawer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.ui.utils.extension.colorCompat


/**
 * Draw a complete event of a timeline.
 */
class CompleteEventDrawer(context: Context) {

    private val strokeWidth = context.resources.getDimension(R.dimen.timeline_stroke_width)
    private val completeHeight = context.resources.getDimension(R.dimen.timeline_complete_height)
    private val strokeColor = context.colorCompat(R.color.timeline_stroke_color)

    private val paint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        color = strokeColor
        strokeWidth = this@CompleteEventDrawer.strokeWidth
        style = Paint.Style.STROKE
    }

    /**
     * Draw the complete event on the canvas
     * @param canvas The canvas to draw on
     * @param x The x position of the complete event
     * @param y The y position of the center of the complete event
     */
    fun draw(canvas: Canvas, x: Float, y: Float) {
        canvas.drawLine(
                x,
                y - completeHeight / 2,
                x,
                y + completeHeight / 2,
                paint
        )
    }
}