package aodev.blue.rxsandbox.model.operator.filtering

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.Event
import aodev.blue.rxsandbox.model.Termination
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.utils.clamp


class DebounceOperator<T>(private val duration: Float) : Operator<T, T> {

    // TODO Verify the behavior of the debounce with an error

    override fun apply(timeline: Timeline<T>): Timeline<T> {
        val firstEvents = timeline.sortedEvents
                .zip(timeline.sortedEvents.drop(1))
                .filter { (event, next) -> next.time - event.time >= duration }
                .map { (event, _) -> event.moveTo(event.time + duration) }

        val lastEvent: Event<T>? = timeline.sortedEvents.lastOrNull()?.let {
            when (timeline.termination) {
                null -> it.moveTo((it.time + duration).clamp(0f, Config.timelineDuration.toFloat()))
                else -> when (timeline.termination.value) {
                    is Termination.Complete -> it.moveTo((it.time + duration).clamp(0f, timeline.termination.time))
                    is Termination.Error -> null
                }
            }
        }

        val events = if (lastEvent != null) firstEvents + lastEvent else firstEvents

        return Timeline(
                events.toSet(),
                timeline.termination
        )
    }

    override fun expression(): String {
            return "debounce(${"%.1f".format(duration)})"
    }
}