package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.CompletableT
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.ParamsObservable


class ObservableIgnoreElements<T> : Operator<T, Unit, ParamsObservable<T>, CompletableT> {

    override fun params(input: List<Timeline<T>>): ParamsObservable<T>? {
        return ParamsObservable.fromInput(input)
    }

    override fun apply(params: ParamsObservable<T>): CompletableT {
        return apply(params.observable)
    }

    fun apply(input: ObservableT<T>): CompletableT {
        val result = when (input.termination) {
            ObservableT.Termination.None -> CompletableT.Result.None
            is ObservableT.Termination.Error -> CompletableT.Result.Error(input.termination.time)
            is ObservableT.Termination.Complete -> CompletableT.Result.Complete(input.termination.time)
        }

        return CompletableT(result)
    }

    override fun expression(): String {
        return "ignoreElements"
    }
}