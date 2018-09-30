package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.ParamsObservable


class ObservableTake<T>(
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
        return if (input.events.size >= count) {
            val events = input.events.take(count)
            ObservableT(
                    events,
                    ObservableT.Termination.Complete(events.lastOrNull()?.time ?: 0f)
            )
        } else {
            input
        }
    }

    override fun expression(): String {
        return "take($count)"
    }
}