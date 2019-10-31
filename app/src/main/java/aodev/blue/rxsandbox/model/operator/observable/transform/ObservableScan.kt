package aodev.blue.rxsandbox.model.operator.observable.transform

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.functions.Function2
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableScan<T : Any, out R : Any>(
        private val initialValue: R,
        private val operation: Function2<R, T, R>
) : Operator {

    fun apply(input: ObservableT<T>): ObservableT<R> {
        return ObservableT(
                input.events.fold(listOf(ObservableT.Event(0f, initialValue))) { acc, event ->
                    val newValue = operation.apply(acc.last().value, event.value)
                    acc + ObservableT.Event(event.time, newValue)
                },
                input.termination
        )
    }

    override val expression: String = "scan($initialValue) { ${operation.expression} }"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}scan.html"
}
