package aodev.blue.rxsandbox.ui.widget.timeline.drawer

import android.graphics.Canvas
import android.graphics.Paint
import android.view.Gravity
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.RadioButton
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.ui.utils.extension.getColorCompat
import aodev.blue.rxsandbox.ui.widget.model.TimelineConnection
import aodev.blue.rxsandbox.ui.widget.model.TimelineSelection
import java.lang.IllegalStateException


class ConnectionDrawer(
        private val container: FrameLayout,
        private val onSelected: () -> Unit
) {

    var isLtr: Boolean = true

    var selection: TimelineSelection = TimelineSelection.None
        set(value) {
            field = value
            updateSelectionView(value)
        }

    private var selectionView: RadioButton? = null

    // Resources
    private val context = container.context
    private val resources = context.resources
    private val paddingStart = resources.getDimension(R.dimen.timeline_padding_start)
    private val paddingEnd = resources.getDimension(R.dimen.timeline_padding_end)
    private val connectionCircleSize = resources.getDimension(R.dimen.timeline_connection_circle_size)
    private val connectionStrokeWidth = resources.getDimension(R.dimen.timeline_connection_stroke_width)
    private val checkBoxSizeTotal = resources.getDimensionPixelSize(R.dimen.timeline_selection_checkbox_size_total)
    private val checkBoxSizeActual = resources.getDimensionPixelSize(R.dimen.timeline_selection_checkbox_size_actual)
    private val connectionColor = context.getColorCompat(R.color.timeline_connection_color)

    // Paints
    private val connectionPaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        color = connectionColor
        strokeWidth = connectionStrokeWidth
    }

    fun draw(canvas: Canvas, downConnection: TimelineConnection) {
        drawDownConnection(canvas, downConnection)
        drawSelection(canvas)
    }

    private fun drawDownConnection(canvas: Canvas, downConnection: TimelineConnection) {
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

    private fun drawSelection(canvas: Canvas) {
        val selection = selection
        when (selection) {
            is TimelineSelection.Alone -> {
                val middleHeight = canvas.height.toFloat() / 2
                val x = paddingStart / 2
                canvas.drawLine(x, 0f, x, middleHeight, connectionPaint)
                canvas.drawCircle(x, middleHeight, connectionCircleSize, connectionPaint)
            }
        }
        if (selection is TimelineSelection.Checkbox) {
            if (selection.connected) {
                val middleHeight = canvas.height.toFloat() / 2
                val x = paddingStart / 2

                if (selection.selected) {
                    canvas.drawLine(x, 0f, x, middleHeight, connectionPaint)
                } else {
                    val firstLineBottom = middleHeight - checkBoxSizeActual / 2
                    val secondLineTop = middleHeight + checkBoxSizeActual / 2
                    val secondLineBottom = canvas.height.toFloat()

                    canvas.drawLine(x, 0f, x, firstLineBottom, connectionPaint)
                    canvas.drawLine(x, secondLineTop, x, secondLineBottom, connectionPaint)
                }
            }
        }
    }

    private fun updateSelectionView(selection: TimelineSelection) {
        when (selection) {
            is TimelineSelection.None,
            is TimelineSelection.Alone -> {
                selectionView?.let {
                    container.removeView(it)
                    this.selectionView = null
                }
            }
            is TimelineSelection.Checkbox -> {
                val selectionView = selectionView ?: run {
                    createSelectionCheckBox().also { checkBoxView ->
                        container.addView(checkBoxView)
                        selectionView = checkBoxView
                    }
                }
                selectionView.isChecked = selection.selected
            }
        }
    }

    private fun createSelectionCheckBox() : RadioButton {
        return RadioButton(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                    checkBoxSizeTotal,
                    checkBoxSizeTotal
            ).apply {
                gravity = Gravity.CENTER_VERTICAL
                marginStart = (this@ConnectionDrawer.paddingStart.toInt() - checkBoxSizeTotal) / 2
            }
            setOnCheckedChangeListener(this@ConnectionDrawer::onSelectionCheckedChange)
        }
    }

    private fun onSelectionCheckedChange(selectionButton: CompoundButton, isChecked: Boolean) {
        val selection = selection as? TimelineSelection.Checkbox ?: return
        if (!isChecked) {
            // We only notify the listener when the selection is checked
        } else if (selection.selected) {
            // The state is already selected, don't notify the listener
        } else {
            onSelected()
            // Don't let the view decide of its state and wait for a new state to update it
            selectionButton.isChecked = false
        }
    }
}
