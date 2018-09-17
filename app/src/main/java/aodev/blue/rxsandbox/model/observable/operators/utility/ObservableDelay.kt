package aodev.blue.rxsandbox.model.observable.operators.utility

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableDelay<T>(
        private val delay: Float
) : Operator<ObservableTimeline<T>, ObservableTimeline<T>> {

    init {
        require(delay >= 0)
    }

    override fun apply(input: ObservableTimeline<T>): ObservableTimeline<T> {
        val delayedEvents = input.events.mapNotNull { event ->
            val newTime = event.time + delay
            if (newTime <= Config.timelineDuration) {
                event.moveTo(newTime)
            } else {
                null
            }
        }.toSet()

        val delayedTermination = when (input.termination) {
            ObservableTermination.None -> ObservableTermination.None
            is ObservableTermination.Error -> {
                val newTime = input.termination.time + delay
                if (newTime <= Config.timelineDuration) {
                    ObservableTermination.Error(newTime)
                } else {
                    ObservableTermination.None
                }
            }
            is ObservableTermination.Complete -> {
                val newTime = input.termination.time + delay
                if (newTime <= Config.timelineDuration) {
                    ObservableTermination.Complete(newTime)
                } else {
                    ObservableTermination.None
                }
            }
        }

        return ObservableTimeline(delayedEvents, delayedTermination)
    }

    override fun expression(): String {
        return "delay(${"%.1f".format(delay)})"
    }
}