package aodev.blue.rxsandbox.model.observable.operators.filter

import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableTake<T>(
        private val count: Int
) : Operator<ObservableTimeline<T>, ObservableTimeline<T>> {

    init {
        require(count >= 0)
    }

    override fun apply(input: ObservableTimeline<T>): ObservableTimeline<T> {
        return if (input.events.size >= count) {
            val events = input.events.take(count)
            ObservableTimeline(
                    events,
                    ObservableTermination.Complete(events.lastOrNull()?.time ?: 0f)
            )
        } else {
            input
        }
    }

    override fun expression(): String {
        return "take($count)"
    }
}