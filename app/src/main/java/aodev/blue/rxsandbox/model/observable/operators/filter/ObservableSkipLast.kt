package aodev.blue.rxsandbox.model.observable.operators.filter

import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableSkipLast<T>(
        private val count: Int
) : Operator<ObservableTimeline<T>, ObservableTimeline<T>> {

    init {
        require(count >= 0)
    }

    // TODO verify the behavior of the skipLast with an error

    override fun apply(input: ObservableTimeline<T>): ObservableTimeline<T> {
        return when (input.termination) {
            ObservableTermination.None -> ObservableTimeline(emptySet(), input.termination)
            is ObservableTermination.Error -> ObservableTimeline(emptySet(), input.termination)
            is ObservableTermination.Complete -> {
                val events = input.sortedEvents.dropLast(count)
                        .map { it.moveTo(input.termination.time) }
                        .toSet()
                ObservableTimeline(events.toSet(), input.termination)
            }
        }
    }

    override fun expression(): String {
        return "skipLast($count)"
    }
}