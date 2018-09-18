package aodev.blue.rxsandbox.model.observable.operators.filter

import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableDistinctUntilChanged<T> : Operator<ObservableTimeline<T>, ObservableTimeline<T>> {

    override fun apply(input: ObservableTimeline<T>): ObservableTimeline<T> {
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

        return ObservableTimeline(
                events = events,
                termination = input.termination
        )
    }

    override fun expression(): String {
        return "distinctUntilChanged"
    }
}