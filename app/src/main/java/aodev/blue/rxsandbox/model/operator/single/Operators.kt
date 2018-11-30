@file:Suppress("unused")

package aodev.blue.rxsandbox.model.operator.single

import aodev.blue.rxsandbox.model.SingleX
import aodev.blue.rxsandbox.model.functions.Function
import aodev.blue.rxsandbox.model.operator.single.create.SingleJust
import aodev.blue.rxsandbox.model.operator.single.transform.SingleMap
import aodev.blue.rxsandbox.model.operator.single.utility.SingleDelay


// region Create

fun <T : Any> SingleX.Companion.just(element: T): SingleX<T> {
    val operator = SingleJust(element)
    return SingleX.Result(operator, emptyList(), operator::apply)
}

// endregion

// region Transform

fun <T : Any, R : Any> SingleX<T>.map(mapping: Function<T, R>): SingleX<R> {
    val operator = SingleMap(mapping)
    return SingleX.Result(operator, listOf(this)) {
        operator.apply(singleT())
    }
}

// endregion

// region Utility

fun <T : Any> SingleX<T>.delay(delay: Float): SingleX<T> {
    val operator = SingleDelay<T>(delay)
    return SingleX.Result(operator, listOf(this)) {
        operator.apply(singleT())
    }
}

// endregion
