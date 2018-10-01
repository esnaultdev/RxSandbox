package aodev.blue.rxsandbox.ui.widget.timeline.drawer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Typeface
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.ui.utils.extension.getColorCompat


/**
 * Draw a timeline line with its arrow and the type text.
 */
class TimelineLineDrawer(
        context: Context,
        private val typeText: String
) {

    var isLtr: Boolean = true

    // Resources
    private val padding = context.resources.getDimension(R.dimen.timeline_padding)
    private val innerPaddingStart = context.resources.getDimension(R.dimen.timeline_padding_inner_start)
    private val innerPaddingEnd = context.resources.getDimension(R.dimen.timeline_padding_inner_end)
    private val strokeWidth = context.resources.getDimension(R.dimen.timeline_stroke_width)
    private val lineDashSize = context.resources.getDimension(R.dimen.timeline_line_dash_size)
    private val typeTextSize = context.resources.getDimension(R.dimen.timeline_type_text_size)
    private val typeTextPadding = context.resources.getDimension(R.dimen.timeline_type_text_padding)
    private val arrowWidth = context.resources.getDimension(R.dimen.timeline_arrow_width)
    private val arrowHeight = context.resources.getDimension(R.dimen.timeline_arrow_height)
    private val strokeColor = context.getColorCompat(R.color.timeline_stroke_color)
    private val typeTextColor = context.getColorCompat(R.color.timeline_type_text_color)

    // Paints
    private val strokePaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        color = strokeColor
        strokeWidth = this@TimelineLineDrawer.strokeWidth
        style = Paint.Style.STROKE
    }
    private val arrowPaint = Paint().apply {
        color = strokeColor
        style = Paint.Style.FILL_AND_STROKE
    }
    private val typeTextPaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        color = typeTextColor
        textSize = typeTextSize
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
    }

    // Draw
    private val arrowPath = Path()
    private val linePath = Path()
    private val textBoundsRect = Rect()

    // region Size management

    fun onSizeChanged(width: Int, height: Int) {
        computePaths(width, height)
    }

    private fun computePaths(width: Int, height: Int) {
        arrowPath.run {
            reset()

            // Starting from the pointy end
            if (isLtr) {
                moveTo(width - padding, height.toFloat() / 2)
                rLineTo(-arrowWidth, -arrowHeight / 2)
                rLineTo(0f, arrowHeight)
                rLineTo(arrowWidth, -arrowHeight / 2)
                close()
            } else {
                moveTo(padding, height.toFloat() / 2)
                rLineTo(arrowWidth, -arrowHeight / 2)
                rLineTo(0f, arrowHeight)
                rLineTo(-arrowWidth, -arrowHeight / 2)
                close()
            }
        }

        linePath.run {
            reset()

            val initialX = if (isLtr) padding else width - padding
            moveTo(initialX, height.toFloat() / 2)

            // Start dashed line
            val dashDx = if (isLtr) lineDashSize else -lineDashSize
            val startDashCount = (innerPaddingStart / (2 * lineDashSize)).toInt()
            repeat(startDashCount) {
                rLineTo(dashDx, 0f)
                rMoveTo(dashDx, 0f)
            }

            // Continuous line
            val lineWidth = width - 2 * padding - innerPaddingStart - innerPaddingEnd
            val lineDx = if (isLtr) lineWidth else -lineWidth
            rLineTo(lineDx, 0f)

            // End dashed line
            // We subtract one to keep the arrow pointy
            val endDashCount = ((innerPaddingEnd) / (2 * lineDashSize)).toInt() - 1
            repeat(endDashCount) {
                rMoveTo(dashDx, 0f)
                rLineTo(dashDx, 0f)
            }
        }
    }

    //endregion

    //region Drawing

    fun draw(canvas: Canvas) {
        drawLine(canvas)
        drawTypeText(canvas)
    }

    private fun drawLine(canvas: Canvas) {
        canvas.drawPath(arrowPath, arrowPaint)
        canvas.drawPath(linePath, strokePaint)
    }

    private fun drawTypeText(canvas: Canvas) {
        val text = typeText

        typeTextPaint.getTextBounds(text, 0, text.length, textBoundsRect)

        val textX = if (isLtr) {
            canvas.width - typeTextPadding - textBoundsRect.width().toFloat() - textBoundsRect.left
        } else {
            typeTextPadding + textBoundsRect.left
        }
        val textY = canvas.height - typeTextPadding - textBoundsRect.bottom

        canvas.drawText(text, textX, textY, typeTextPaint)
    }

    //endregion
}