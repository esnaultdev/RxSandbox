package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operation.predicate.Predicate
import aodev.blue.rxsandbox.model.operator.Input


class ObservableFilter<T>(private val predicate: Predicate<T>) : Operator<T, T> {

    override fun apply(input: List<Timeline<T>>): Timeline<T>? {
        return Input.Observable.from(input) {
            apply(it)
        }
    }

    fun apply(input: ObservableT<T>): ObservableT<T> {
        return ObservableT(
                input.events.filter { predicate.check(it.value) },
                input.termination
        )
    }

    override val expression: String = "filter { ${predicate.expression} }"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}filter.html"
}