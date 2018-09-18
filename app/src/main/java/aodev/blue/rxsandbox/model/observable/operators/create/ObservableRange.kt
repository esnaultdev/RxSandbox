package aodev.blue.rxsandbox.model.observable.operators.create

import aodev.blue.rxsandbox.model.Creator
import aodev.blue.rxsandbox.model.observable.ObservableEvent
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableRange(
        private val from: Int,
        private val to: Int
) : Creator<ObservableTimeline<Int>> {

    init {
        require(from >= 0)
        require(to >= 0)
    }

    override fun create(): ObservableTimeline<Int> {
        return ObservableTimeline(
                (from..to).map { ObservableEvent(0f, it) },
                ObservableTermination.Complete(0f)
        )
    }

    override fun expression(): String {
        return "range($from, $to)"
    }
}