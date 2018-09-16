package aodev.blue.rxsandbox.model.observable.operators.create

import aodev.blue.rxsandbox.model.Creator
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableEmpty<T> : Creator<ObservableTimeline<T>> {

    override fun create(): ObservableTimeline<T> {
        return ObservableTimeline(
                emptySet(),
                ObservableTermination.Complete(0f)
        )
    }

    override fun expression(): String {
        return "empty"
    }
}