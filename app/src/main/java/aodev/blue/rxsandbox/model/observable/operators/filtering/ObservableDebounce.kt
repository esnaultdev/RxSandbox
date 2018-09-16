package aodev.blue.rxsandbox.model.observable.operators.filtering

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.observable.ObservableEvent
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline
import aodev.blue.rxsandbox.utils.clamp


class ObservableDebounce<T>(
        private val duration: Float
) : Operator<ObservableTimeline<T>, ObservableTimeline<T>> {

    // TODO Verify the behavior of the debounce with an error

    override fun apply(input: ObservableTimeline<T>): ObservableTimeline<T> {
        val firstEvents = input.sortedEvents
                .zip(input.sortedEvents.drop(1))
                .filter { (event, next) -> next.time - event.time >= duration }
                .map { (event, _) -> event.moveTo(event.time + duration) }

        val lastEvent: ObservableEvent<T>? = input.sortedEvents.lastOrNull()?.let {
            when (input.termination) {
                ObservableTermination.None -> {
                    val newTime = (it.time + duration).clamp(0f, Config.timelineDuration.toFloat())
                    it.moveTo(newTime)
                }
                is ObservableTermination.Complete -> {
                    val newTime = (it.time + duration).clamp(0f, input.termination.time)
                    it.moveTo(newTime)
                }
                is ObservableTermination.Error -> null
            }
        }

        val events = if (lastEvent != null) firstEvents + lastEvent else firstEvents

        return ObservableTimeline(
                events.toSet(),
                input.termination
        )
    }

    override fun expression(): String {
            return "debounce(${"%.1f".format(duration)})"
    }
}