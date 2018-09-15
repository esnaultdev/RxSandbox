package aodev.blue.rxsandbox.model.observable.operators.transform

import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.observable.ObservableEvent
import aodev.blue.rxsandbox.model.observable.ObservableTimeline
import aodev.blue.rxsandbox.model.operations.mapping.Mapping


class ObservableMap<in T, out R>(
        private val mapping: Mapping<T, R>
) : Operator<ObservableTimeline<T>, ObservableTimeline<R>> {

    override fun apply(input: ObservableTimeline<T>): ObservableTimeline<R> {
        return ObservableTimeline(
                input.events.map { ObservableEvent(it.time, mapping.map(it.value)) }.toSet(),
                input.termination
        )
    }

    override fun expression(): String {
        return "map { ${mapping.expression()} }"
    }
}
