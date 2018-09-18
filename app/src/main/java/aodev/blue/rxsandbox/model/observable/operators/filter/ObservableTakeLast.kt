package aodev.blue.rxsandbox.model.observable.operators.filter

import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableTakeLast<T>(
        private val count: Int
) : Operator<ObservableTimeline<T>, ObservableTimeline<T>> {

    init {
        require(count >= 0)
    }

    // TODO verify the behavior of the takeLast with an error

    override fun apply(input: ObservableTimeline<T>): ObservableTimeline<T> {
        return when (input.termination) {
            ObservableTermination.None -> ObservableTimeline(emptyList(), input.termination)
            is ObservableTermination.Error -> ObservableTimeline(emptyList(), input.termination)
            is ObservableTermination.Complete -> {
                val events = input.events.takeLast(count)
                        .map { it.moveTo(input.termination.time) }
                ObservableTimeline(events, input.termination)
            }
        }
    }

    override fun expression(): String {
        return "takeLast($count)"
    }
}