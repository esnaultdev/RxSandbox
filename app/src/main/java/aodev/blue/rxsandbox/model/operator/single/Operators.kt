package aodev.blue.rxsandbox.model.operator.single

import aodev.blue.rxsandbox.model.SingleX
import aodev.blue.rxsandbox.model.functions.functionOf
import aodev.blue.rxsandbox.model.operator.single.create.SingleJust
import aodev.blue.rxsandbox.model.operator.single.transform.SingleMap
import aodev.blue.rxsandbox.model.operator.single.utility.SingleDelay


fun <T : Any> SingleX.Companion.just(element: T): SingleX<T> {
    val operator = SingleJust(element)
    return SingleX.Result(operator, emptyList()) {
        operator.apply()
    }
}

fun <T : Any, R : Any> SingleX<T>.map(
        expression: String,
        mapping: (T) -> R
): SingleX<R> {
    val operator = SingleMap(functionOf(expression, mapping))
    return SingleX.Result(operator, listOf(this)) {
        operator.apply(this.invoke())
    }
}

fun <T : Any> SingleX<T>.delay(delay: Float): SingleX<T> {
    val operator = SingleDelay<T>(delay)
    return SingleX.Result(operator, listOf(this)) {
        operator.apply(this.invoke())
    }
}
