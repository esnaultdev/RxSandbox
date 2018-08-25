package aodev.blue.rxsandbox.ui.utils.extension

import android.view.View


val View.isLtr: Boolean
    get() = this.layoutDirection == View.LAYOUT_DIRECTION_LTR