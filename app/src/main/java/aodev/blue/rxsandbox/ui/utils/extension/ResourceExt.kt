package aodev.blue.rxsandbox.ui.utils.extension

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


@ColorInt
fun Context.colorCompat(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}