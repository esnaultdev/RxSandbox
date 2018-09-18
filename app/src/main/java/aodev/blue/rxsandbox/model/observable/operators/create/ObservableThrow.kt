package aodev.blue.rxsandbox.model.observable.operators.create

import aodev.blue.rxsandbox.model.Creator
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableThrow<T> : Creator<ObservableTimeline<T>> {

    override fun create(): ObservableTimeline<T> {
        return ObservableTimeline(
                emptyList(),
                ObservableTermination.Error(0f)
        )
    }

    override fun expression(): String {
        return "throw"
    }
}
