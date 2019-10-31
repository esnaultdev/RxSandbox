@file:Suppress("unused")

package aodev.blue.rxsandbox.model.operator.completable

import aodev.blue.rxsandbox.model.CompletableT
import aodev.blue.rxsandbox.model.CompletableX
import aodev.blue.rxsandbox.model.InnerReactiveTypeX
import aodev.blue.rxsandbox.model.operator.completable.utility.CompletableDelay


// region Input

fun CompletableX.Companion.inputOf(
        result: CompletableT.Result
): CompletableX = CompletableX(InnerReactiveTypeX.Input(CompletableT(result)))

// endregion

// region Utility

fun CompletableX.delay(delay: Float): CompletableX {
    val operator = CompletableDelay(delay)
    return CompletableX(
            InnerReactiveTypeX.Result(operator, listOf(this)) {
                operator.apply(innerX.timeline())
            }
    )
}

// endregion
