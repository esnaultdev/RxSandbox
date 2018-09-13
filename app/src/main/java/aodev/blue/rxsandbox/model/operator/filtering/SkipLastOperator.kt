package aodev.blue.rxsandbox.model.operator.filtering

import aodev.blue.rxsandbox.model.Event
import aodev.blue.rxsandbox.model.Termination
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator


class SkipLastOperator<T>(private val count: Int) : Operator<T, T> {

    override fun apply(timeline: Timeline<T>): Timeline<T> {
        return when (timeline.termination) {
            null -> Timeline(emptySet(), null)
            else -> when (timeline.termination.value) {
                is Termination.Error -> Timeline(emptySet(), timeline.termination)
                is Termination.Complete -> {
                    // This is the representation on the ReactiveX doc but the event times are
                    // really weird. All the items should be emitted when the source completes.
                    val events = timeline.sortedEvents.zip(timeline.sortedEvents.drop(count))
                            .map { Event(it.second.time, it.first.value) }
                            .toSet()
                    Timeline(events.toSet(), timeline.termination)
                }
            }
        }
    }

    override fun expression(): String {
        return "skipLast($count)"
    }
}