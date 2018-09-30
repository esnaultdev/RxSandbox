package aodev.blue.rxsandbox.model.operator.observable.transform

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operation.mapping.Mapping
import aodev.blue.rxsandbox.model.operator.ParamsObservable


class ObservableMap<T, out R>(
        private val mapping: Mapping<T, R>
) : Operator<T, R, ParamsObservable<T>, ObservableT<R>> {

    override fun params(input: List<Timeline<T>>): ParamsObservable<T>? {
        return ParamsObservable.fromInput(input)
    }

    override fun apply(params: ParamsObservable<T>): ObservableT<R> {
        return apply(params.observable)
    }

    fun apply(input: ObservableT<T>): ObservableT<R> {
        return ObservableT(
                input.events.map { ObservableT.Event(it.time, mapping.map(it.value)) },
                input.termination
        )
    }

    override fun expression(): String {
        return "map { ${mapping.expression()} }"
    }
}
