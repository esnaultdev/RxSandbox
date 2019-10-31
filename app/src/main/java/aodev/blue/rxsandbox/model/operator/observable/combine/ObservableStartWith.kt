package aodev.blue.rxsandbox.model.operator.observable.combine

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.operator.Operator

class ObservableStartWith<T : Any>(val value: T) : Operator {

    fun apply(input: ObservableT<T>): ObservableT<T> {
        val events = mutableListOf(
                ObservableT.Event(0f, value)
        ).apply {
            addAll(input.events)
        }
        return input.copy(events = events)
    }

    override val expression: String = "startWith($value)"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}startwith.html"
}