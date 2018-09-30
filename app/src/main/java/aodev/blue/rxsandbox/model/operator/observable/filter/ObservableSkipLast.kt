package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.ParamsObservable


class ObservableSkipLast<T>(
        private val count: Int
) : Operator<T, T, ParamsObservable<T>, ObservableT<T>> {

    init {
        require(count >= 0)
    }

    // TODO verify the behavior of the skipLast with an error

    override fun params(input: List<Timeline<T>>): ParamsObservable<T>? {
        return ParamsObservable.fromInput(input)
    }

    override fun apply(params: ParamsObservable<T>): ObservableT<T> {
        return apply(params.observable)
    }

    fun apply(input: ObservableT<T>): ObservableT<T> {
        return when (input.termination) {
            ObservableT.Termination.None -> ObservableT(emptyList(), input.termination)
            is ObservableT.Termination.Error -> ObservableT(emptyList(), input.termination)
            is ObservableT.Termination.Complete -> {
                val events = input.events.dropLast(count)
                        .map { it.moveTo(input.termination.time) }
                ObservableT(events, input.termination)
            }
        }
    }

    override fun expression(): String {
        return "skipLast($count)"
    }
}