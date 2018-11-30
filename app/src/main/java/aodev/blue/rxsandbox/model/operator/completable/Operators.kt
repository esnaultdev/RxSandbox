@file:Suppress("unused")

package aodev.blue.rxsandbox.model.operator.completable

import aodev.blue.rxsandbox.model.CompletableX
import aodev.blue.rxsandbox.model.operator.completable.utility.CompletableDelay


// region Utility

fun CompletableX.delay(delay: Float): CompletableX {
    val operator = CompletableDelay(delay)
    return CompletableX.Result(operator, listOf(this)) {
        operator.apply(completableT())
    }
}

// endregion
