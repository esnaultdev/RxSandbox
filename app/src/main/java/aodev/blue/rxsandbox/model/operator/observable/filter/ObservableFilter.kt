package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.functions.Predicate
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableFilter<T : Any>(private val predicate: Predicate<T>) : Operator {

    fun apply(input: ObservableT<T>): ObservableT<T> {
        return ObservableT(
                input.events.filter { predicate.test(it.value) },
                input.termination
        )
    }

    override val expression: String = "filter { ${predicate.expression} }"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}filter.html"
}