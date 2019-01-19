@file:Suppress("unused")

package aodev.blue.rxsandbox.model.operator.maybe

import aodev.blue.rxsandbox.model.InnerReactiveTypeX
import aodev.blue.rxsandbox.model.MaybeT
import aodev.blue.rxsandbox.model.MaybeX
import aodev.blue.rxsandbox.model.operator.maybe.utility.MaybeDelay


// region Input

fun <T : Any> MaybeX.Companion.inputOf(
        result: MaybeT.Result<T>
): MaybeX<T> {
    val innerX = InnerReactiveTypeX.Input(MaybeT(result))
    return MaybeX(innerX)
}

// endregion

// region Utility

fun <T : Any> MaybeX<T>.delay(delay: Float): MaybeX<T> {
    val operator = MaybeDelay<T>(delay)
    val innerX = InnerReactiveTypeX.Result(operator, listOf(this)) {
        operator.apply(innerX.timeline())
    }
    return MaybeX(innerX)
}

// endregion
