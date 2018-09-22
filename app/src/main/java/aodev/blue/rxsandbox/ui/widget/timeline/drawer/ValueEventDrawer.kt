package aodev.blue.rxsandbox.ui.widget.timeline.drawer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.ui.utils.extension.colorCompat


/**
 * Draw a value event of a timeline.
 */
class ValueEventDrawer(context: Context) {

    private val strokeWidth = context.resources.getDimension(R.dimen.timeline_stroke_width)
    private val eventSize = context.resources.getDimension(R.dimen.timeline_event_size)
    private val eventTextSize = context.resources.getDimension(R.dimen.timeline_event_text_size)
    private val strokeColor = context.colorCompat(R.color.timeline_stroke_color)
    private val eventFillColor = context.colorCompat(R.color.timeline_event_fill_color)
    private val eventTextColor = context.colorCompat(R.color.timeline_event_text_color)

    private val strokePaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        color = strokeColor
        strokeWidth = this@ValueEventDrawer.strokeWidth
        style = Paint.Style.STROKE
    }
    private val eventFillPaint = Paint().apply {
        color = eventFillColor
        style = Paint.Style.FILL
    }
    private val eventTextPaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        color = eventTextColor
        textSize = eventTextSize
    }

    private val textBoundsRect = Rect()

    /**
     * Draw the value event on the canvas
     * @param canvas The canvas to draw on
     * @param x The x position of the error event
     * @param y The y position of the center of the error event
     * @param value The value of the event to draw
     */
    fun draw(canvas: Canvas, x: Float, y: Float, value: Int) {
        canvas.drawCircle(x, y, eventSize / 2, eventFillPaint)
        canvas.drawCircle(x, y, eventSize / 2, strokePaint)

        val eventText = value.toString()
        eventTextPaint.getTextBounds(eventText, 0, eventText.length, textBoundsRect)
        val textX = x - textBoundsRect.width().toFloat() / 2 - textBoundsRect.left
        val textY = y + textBoundsRect.height().toFloat() / 2 - textBoundsRect.bottom

        canvas.drawText(eventText, textX, textY, eventTextPaint)
    }
}