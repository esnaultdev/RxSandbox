package aodev.blue.rxsandbox.model.observable.operators.create

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.observable.ObservableEvent
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableRepeat<T> : Operator<ObservableTimeline<T>, ObservableTimeline<T>> {

    override fun apply(input: ObservableTimeline<T>): ObservableTimeline<T> {
        return when (input.termination) {
            ObservableTermination.None -> input
            is ObservableTermination.Error -> input
            is ObservableTermination.Complete -> {
                if (input.termination.time == 0f) {
                    ObservableTimeline(emptySet(), ObservableTermination.None)
                } else {
                    val repeatTime = input.termination.time
                    val repeatCount = (Config.timelineDuration / repeatTime).toInt()
                    val events = (0..repeatCount).map { repeatIndex ->
                        input.events.mapNotNull { event ->
                            val newTime = event.time + repeatIndex * repeatTime
                            if (newTime <= Config.timelineDuration) {
                                ObservableEvent(newTime, event.value)
                            } else {
                                null
                            }
                        }
                    }.flatten().toSet()

                    ObservableTimeline(
                        events,
                        ObservableTermination.None
                    )
                }
            }
        }
    }

    override fun expression(): String {
        return "repeat"
    }
}