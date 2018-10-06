package aodev.blue.rxsandbox.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri


/**
 * Open a link in the device browser.
 * Return true if the browser could be opened, false if no browser is present on the device.
 */
fun Activity.openInBrowser(url: String): Boolean {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)

    return try {
        startActivity(intent)
        true
    } catch (ex: ActivityNotFoundException) {
        false
    }
}

