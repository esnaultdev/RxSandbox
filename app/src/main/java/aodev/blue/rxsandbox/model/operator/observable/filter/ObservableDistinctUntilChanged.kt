package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.ParamsObservable


class ObservableDistinctUntilChanged<T> : Operator<T, T, ParamsObservable<T>, ObservableT<T>> {

    override fun params(input: List<Timeline<T>>): ParamsObservable<T>? {
        return ParamsObservable.fromInput(input)
    }

    override fun apply(params: ParamsObservable<T>): ObservableT<T> {
        return apply(params.observable)
    }

    fun apply(input: ObservableT<T>): ObservableT<T> {
        val events = when (input.events.size) {
            0 -> emptyList()
            1 -> listOf(input.events.first())
            else -> {
                val firstEvent = input.events.first()
                val otherEvents = input.events.drop(1)
                        .zip(input.events)
                        .filter { it.first.value != it.second.value }
                        .map { it.first }

                mutableListOf(firstEvent).apply { addAll(otherEvents) }
            }
        }

        return ObservableT(
                events = events,
                termination = input.termination
        )
    }

    override fun expression(): String {
        return "distinctUntilChanged"
    }
}