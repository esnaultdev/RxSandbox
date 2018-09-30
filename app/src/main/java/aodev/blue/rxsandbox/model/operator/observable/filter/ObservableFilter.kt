package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operation.predicate.Predicate
import aodev.blue.rxsandbox.model.operator.ParamsObservable


class ObservableFilter<T>(
        private val predicate: Predicate<T>
) : Operator<T, T, ParamsObservable<T>, ObservableT<T>> {

    override fun params(input: List<Timeline<T>>): ParamsObservable<T>? {
        return ParamsObservable.fromInput(input)
    }

    override fun apply(params: ParamsObservable<T>): ObservableT<T> {
        return apply(params.observable)
    }

    fun apply(input: ObservableT<T>): ObservableT<T> {
        return ObservableT(
                input.events.filter { predicate.check(it.value) },
                input.termination
        )
    }

    override fun expression(): String {
        return "filter { ${predicate.expression()} }"
    }
}