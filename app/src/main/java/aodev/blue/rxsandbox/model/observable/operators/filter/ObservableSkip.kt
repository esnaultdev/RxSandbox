package aodev.blue.rxsandbox.model.observable.operators.filter

import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableSkip<T>(
        private val count: Int
) : Operator<ObservableTimeline<T>, ObservableTimeline<T>> {

    init {
        require(count >= 0)
    }

    override fun apply(input: ObservableTimeline<T>): ObservableTimeline<T> {
        return ObservableTimeline(
                input.sortedEvents.drop(count).toSet(),
                input.termination
        )
    }

    override fun expression(): String {
        return "skip($count)"
    }
}