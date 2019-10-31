package aodev.blue.rxsandbox.ui.widget.utils

import android.graphics.Paint
import android.graphics.Rect
import kotlin.math.floor

private val boundsRect: Rect by lazy { Rect() }

/**
 * Based on https://stackoverflow.com/a/21895626/9198676
 * Sets the text size for a Paint object so a given string of text will be a given width
 *
 * @param paint the Paint to set the text size for
 * @param desiredWidth the desired width
 * @param text the text that should be that width
 */
fun Paint.setTextSizeForWidth(desiredWidth: Float, text: String) {
    if (text.isEmpty()) return

    // Pick a reasonably large value for the test. Larger values produce more accurate results,
    // but may cause problems with hardware acceleration
    val testTextSize = 48f

    // Get the bounds of the text, using our testTextSize
    textSize = testTextSize
    getTextBounds(text, 0, text.length, boundsRect)

    // Calculate the desired size as a proportion of our testTextSize
    val desiredTextSize = floor(testTextSize * desiredWidth / boundsRect.width())

    // Set the paint for that size
    textSize = desiredTextSize
}
