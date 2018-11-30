package aodev.blue.rxsandbox.model.operator.observable.transform

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.functions.Function
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableMap<T : Any, out R : Any>(private val mapping: Function<T, R>) : Operator {

    fun apply(input: ObservableT<T>): ObservableT<R> {
        return ObservableT(
                input.events.map { ObservableT.Event(it.time, mapping.apply(it.value)) },
                input.termination
        )
    }

    override val expression: String = "map { ${mapping.expression} }"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}map.html"
}
