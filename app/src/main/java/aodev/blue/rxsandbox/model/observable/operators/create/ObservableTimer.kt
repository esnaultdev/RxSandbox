package aodev.blue.rxsandbox.model.observable.operators.create

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.Creator
import aodev.blue.rxsandbox.model.observable.ObservableEvent
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableTimer(private val delay: Float) : Creator<ObservableTimeline<Int>> {

    init {
        require(delay >= 0)
    }

    override fun create(): ObservableTimeline<Int> {
        return if (delay > Config.timelineDuration) {
            ObservableTimeline(emptySet(), ObservableTermination.None)
        } else {
            ObservableTimeline(
                    setOf(ObservableEvent(delay, 0)),
                    ObservableTermination.Complete(delay)
            )
        }
    }

    override fun expression(): String {
        return "timer(${"%.1f".format(delay)})"
    }
}