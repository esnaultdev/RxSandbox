package aodev.blue.rxsandbox.model.operator.observable.transform

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.functions.Function
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.Input


class ObservableMap<T : Any, out R : Any>(private val mapping: Function<T, R>) : Operator<T, R> {

    override fun apply(input: List<Timeline<T>>): Timeline<R>? {
        return Input.Observable.from(input) {
            apply(it)
        }
    }

    fun apply(input: ObservableT<T>): ObservableT<R> {
        return ObservableT(
                input.events.map { ObservableT.Event(it.time, mapping.apply(it.value)) },
                input.termination
        )
    }

    override val expression: String = "map { ${mapping.expression} }"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}map.html"
}
