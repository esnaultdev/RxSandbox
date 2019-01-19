@file:Suppress("unused")

package aodev.blue.rxsandbox.model.operator.single

import aodev.blue.rxsandbox.model.InnerReactiveTypeX
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.SingleX
import aodev.blue.rxsandbox.model.functions.Function
import aodev.blue.rxsandbox.model.operator.single.create.SingleJust
import aodev.blue.rxsandbox.model.operator.single.transform.SingleMap
import aodev.blue.rxsandbox.model.operator.single.utility.SingleDelay


// region Input

fun <T : Any> SingleX.Companion.inputOf(
        result: SingleT.Result<T>
): SingleX<T> {
    val innerX = InnerReactiveTypeX.Input(SingleT(result))
    return SingleX(innerX)
}

// endregion

// region Create

fun <T : Any> SingleX.Companion.just(element: T): SingleX<T> {
    val operator = SingleJust(element)
    val innerX = InnerReactiveTypeX.Result(operator, emptyList(), operator::apply)
    return SingleX(innerX)
}

// endregion

// region Transform

fun <T : Any, R : Any> SingleX<T>.map(mapping: Function<T, R>): SingleX<R> {
    val operator = SingleMap(mapping)
    val innerX = InnerReactiveTypeX.Result(operator, listOf(this)) {
        operator.apply(innerX.timeline())
    }
    return SingleX(innerX)
}

// endregion

// region Utility

fun <T : Any> SingleX<T>.delay(delay: Float): SingleX<T> {
    val operator = SingleDelay<T>(delay)
    val innerX = InnerReactiveTypeX.Result(operator, listOf(this)) {
        operator.apply(innerX.timeline())
    }
    return SingleX(innerX)
}

// endregion
