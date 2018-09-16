package aodev.blue.rxsandbox.model.observable.operators.filtering

import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableDistinctUntilChanged<T> : Operator<ObservableTimeline<T>, ObservableTimeline<T>> {

    override fun apply(input: ObservableTimeline<T>): ObservableTimeline<T> {
        val events = when (input.events.size) {
            0 -> emptySet()
            1 -> setOf(input.sortedEvents.first())
            else -> {
                val firstEvent = input.sortedEvents.first()
                val otherEvents = input.sortedEvents.drop(1)
                        .zip(input.sortedEvents)
                        .filter { it.first.value != it.second.value }
                        .map { it.first }

                mutableSetOf(firstEvent).apply { addAll(otherEvents) }
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