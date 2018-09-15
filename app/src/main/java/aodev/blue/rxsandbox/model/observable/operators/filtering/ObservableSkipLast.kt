package aodev.blue.rxsandbox.model.observable.operators.filtering

import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.observable.ObservableEvent
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableSkipLast<T>(
        private val count: Int
) : Operator<ObservableTimeline<T>, ObservableTimeline<T>> {

    // TODO verify the behavior of the skipLast with an error
    // TODO verify the behavior in a real stream and document the difference if any

    override fun apply(input: ObservableTimeline<T>): ObservableTimeline<T> {
        return when (input.termination) {
            ObservableTermination.None -> ObservableTimeline(emptySet(), input.termination)
            is ObservableTermination.Error -> ObservableTimeline(emptySet(), input.termination)
            is ObservableTermination.Complete -> {
                // This is the representation on the ReactiveX doc but the event times are
                // really weird. All the items should be emitted when the source completes.
                val events = input.sortedEvents
                        .zip(input.sortedEvents.drop(count))
                        .map { ObservableEvent(it.second.time, it.first.value) }
                        .toSet()
                ObservableTimeline(events.toSet(), input.termination)
            }
        }
    }

    override fun expression(): String {
        return "skipLast($count)"
    }
}