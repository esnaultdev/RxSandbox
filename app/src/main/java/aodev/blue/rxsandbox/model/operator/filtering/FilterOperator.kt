package aodev.blue.rxsandbox.model.operator.filtering

import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operations.predicate.Predicate
import aodev.blue.rxsandbox.model.operator.Operator


class FilterOperator<T>(private val predicate: Predicate<T>) : Operator<T, T> {

    override fun apply(timeline: Timeline<T>): Timeline<T> {
        return Timeline(
                timeline.events.filter { predicate.check(it.value) }.toSet(),
                timeline.termination
        )
    }

    override fun expression(): String {
        return "filter { ${predicate.expression()} }"
    }
}