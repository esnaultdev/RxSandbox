package aodev.blue.rxsandbox.ui.utils.extension

import android.view.View


val View.isLtr: Boolean
    get() = this.layoutDirection == View.LAYOUT_DIRECTION_LTR

/**
 * Toggle between [View.VISIBLE] and [View.GONE] based on [visible]
 */
fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}
