package aodev.blue.rxsandbox.model.observable.operators.create

import aodev.blue.rxsandbox.model.Creator
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableNever<T> : Creator<ObservableTimeline<T>> {

    override fun create(): ObservableTimeline<T> {
        return ObservableTimeline(
                emptySet(),
                ObservableTermination.None
        )
    }

    override fun expression(): String {
        return "never"
    }
}