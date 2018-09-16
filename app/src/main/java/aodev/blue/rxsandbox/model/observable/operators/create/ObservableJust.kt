package aodev.blue.rxsandbox.model.observable.operators.create

import aodev.blue.rxsandbox.model.Creator
import aodev.blue.rxsandbox.model.observable.ObservableEvent
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableJust<T>(private vararg val values: T) : Creator<ObservableTimeline<T>> {

    override fun create(): ObservableTimeline<T> {
        return ObservableTimeline(
                values.map { ObservableEvent(0f, it) }.toSet(),
                ObservableTermination.Complete(0f)
        )
    }

    override fun expression(): String {
        return "just(${values.joinToString(", ")})"
    }
}