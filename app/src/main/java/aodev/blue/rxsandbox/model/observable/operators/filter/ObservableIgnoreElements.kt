package aodev.blue.rxsandbox.model.observable.operators.filter

import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.completable.CompletableResult
import aodev.blue.rxsandbox.model.completable.CompletableTimeline
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline


class ObservableIgnoreElements<T> : Operator<ObservableTimeline<T>, CompletableTimeline> {

    override fun apply(input: ObservableTimeline<T>): CompletableTimeline {
        val result = when (input.termination) {
            ObservableTermination.None -> CompletableResult.None
            is ObservableTermination.Error -> CompletableResult.Error(input.termination.time)
            is ObservableTermination.Complete -> CompletableResult.Complete(input.termination.time)
        }

        return CompletableTimeline(result)
    }

    override fun expression(): String {
        return "ignoreElements"
    }
}