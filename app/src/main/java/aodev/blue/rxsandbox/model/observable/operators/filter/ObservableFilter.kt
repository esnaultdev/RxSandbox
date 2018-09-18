package aodev.blue.rxsandbox.model.observable.operators.filter

import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.observable.ObservableTimeline
import aodev.blue.rxsandbox.model.operations.predicate.Predicate


class ObservableFilter<T>(
        private val predicate: Predicate<T>
) : Operator<ObservableTimeline<T>, ObservableTimeline<T>> {

    override fun apply(input: ObservableTimeline<T>): ObservableTimeline<T> {
        return ObservableTimeline(
                input.events.filter { predicate.check(it.value) },
                input.termination
        )
    }

    override fun expression(): String {
        return "filter { ${predicate.expression()} }"
    }
}