package aodev.blue.rxsandbox.ui.widget.timeline.drawer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.ui.utils.extension.getColorCompat


/**
 * Draw a complete event of a timeline.
 */
class CompleteEventDrawer(context: Context) {

    private val strokeWidth = context.resources.getDimension(R.dimen.timeline_stroke_width)
    private val completeHeightMin = context.resources.getDimension(R.dimen.timeline_complete_height_min)
    private val completeHeightMax = context.resources.getDimension(R.dimen.timeline_complete_height_max)
    private val minEventDiff: Float by lazy {
        val valueEventSize = context.resources.getDimension(R.dimen.timeline_event_size)
        valueEventSize / 2
    }
    private val strokeColor = context.getColorCompat(R.color.timeline_stroke_color)

    private val paint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        color = strokeColor
        strokeWidth = this@CompleteEventDrawer.strokeWidth
        style = Paint.Style.STROKE
    }

    /**
     * Draw the complete event on the canvas
     * @param canvas The canvas to draw on
     * @param isLtr The layout direction of the screen
     * @param x The x position of the complete event
     * @param y The y position of the center of the complete event
     * @param previousX The x position of the center of the previous event, if any
     */
    fun draw(canvas: Canvas, isLtr: Boolean, x: Float, y: Float, previousX: Float?) {
        val completeHeight = if (previousX == null) {
            completeHeightMin
        } else {
            val diffX = if (isLtr) x - previousX else previousX - x
            if (diffX  > minEventDiff) {
                completeHeightMin
            } else {
                val t = diffX / minEventDiff
                // Use an easing since the edge we're trying to surpass
                // is the side of a circle that goes up quickly
                val t2 = t * t
                val factor = t2 * t2
                (1 - factor) * completeHeightMax + factor * completeHeightMin
            }
        }

        canvas.drawLine(
                x,
                y - completeHeight / 2,
                x,
                y + completeHeight / 2,
                paint
        )
    }
}
