package aodev.blue.rxsandbox.model.observable.operators.create

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.Creator
import aodev.blue.rxsandbox.model.observable.ObservableEvent
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableInterval(private val interval: Float) : Creator<ObservableTimeline<Int>> {

    init {
        require(interval > 0)
    }

    override fun create(): ObservableTimeline<Int> {
        val count = (Config.timelineDuration / interval).toInt()
        val events = (0..count).map { ObservableEvent(it * interval, it) }

        return ObservableTimeline(
                events.toSet(),
                ObservableTermination.None
        )
    }

    override fun expression(): String {
        return "interval(${"%.1f".format(interval)})"
    }
}