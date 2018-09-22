package aodev.blue.rxsandbox.ui.utils

import android.view.View

/**
 * A basic onMeasure implementation based on a desiredWidth and a desiredHeight
 */
fun basicMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        desiredWidth: Int,
        desiredHeight: Int
): Pair<Int, Int> {
    val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
    val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
    val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
    val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

    val width = when (widthMode) {
        View.MeasureSpec.EXACTLY -> widthSize
        View.MeasureSpec.AT_MOST -> minOf(desiredWidth, widthSize)
        View.MeasureSpec.UNSPECIFIED -> desiredWidth
        else -> throw IllegalArgumentException("Illegal measure spec")
    }

    val height = when (heightMode) {
        View.MeasureSpec.EXACTLY -> heightSize
        View.MeasureSpec.AT_MOST -> minOf(desiredHeight, heightSize)
        View.MeasureSpec.UNSPECIFIED -> desiredHeight
        else -> throw IllegalArgumentException("Illegal measure spec")
    }

    return width to height
}