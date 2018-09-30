package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.ParamsObservable


class ObservableSkip<T>(
        private val count: Int
) : Operator<T, T, ParamsObservable<T>, ObservableT<T>> {

    init {
        require(count >= 0)
    }

    override fun params(input: List<Timeline<T>>): ParamsObservable<T>? {
        return ParamsObservable.fromInput(input)
    }

    override fun apply(params: ParamsObservable<T>): ObservableT<T> {
        return apply(params.observable)
    }

    fun apply(input: ObservableT<T>): ObservableT<T> {
        return ObservableT(
                input.events.drop(count),
                input.termination
        )
    }

    override fun expression(): String {
        return "skip($count)"
    }
}