package aodev.blue.rxsandbox.model.observable.operators.filtering

import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableDistinct<T> : Operator<ObservableTimeline<T>, ObservableTimeline<T>> {

    override fun apply(input: ObservableTimeline<T>): ObservableTimeline<T> {
        val events = input.sortedEvents.distinctBy { it.value }.toSet()

        return ObservableTimeline(
                events = events,
                termination = input.termination
        )
    }

    override fun expression(): String {
        return "distinct"
    }
}