package aodev.blue.rxsandbox.ui.widget.timeline.drawer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.ui.utils.extension.getColorCompat
import aodev.blue.rxsandbox.ui.widget.model.TimelineConnection
import java.lang.IllegalStateException


class ConnectionDrawer(context: Context) {

    var isLtr: Boolean = true

    // Resources
    private val resources = context.resources
    private val paddingEnd = resources.getDimension(R.dimen.timeline_padding_end)
    private val connectionCircleSize = resources.getDimension(R.dimen.timeline_connection_circle_size)
    private val connectionStrokeWidth = resources.getDimension(R.dimen.timeline_connection_stroke_width)
    private val connectionColor = context.getColorCompat(R.color.timeline_connection_color)

    // Paints
    private val connectionPaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        color = connectionColor
        strokeWidth = connectionStrokeWidth
    }

    fun draw(canvas: Canvas, downConnection: TimelineConnection) {
        if (downConnection == TimelineConnection.NONE) return

        val circleX = if (isLtr) {
            canvas.width - paddingEnd / 2
        } else {
            paddingEnd / 2
        }
        val circleY = canvas.height.toFloat() / 2

        // Draw the circle
        canvas.drawCircle(circleX, circleY, connectionCircleSize, connectionPaint)

        // Draw the line
        val lineTop =
        when (downConnection) {
            TimelineConnection.NONE -> throw IllegalStateException() // Already handled before
            TimelineConnection.TOP -> circleY
            TimelineConnection.MIDDLE -> 0f
        }
        canvas.drawLine(circleX, lineTop, circleX, canvas.height.toFloat(), connectionPaint)
    }
}
