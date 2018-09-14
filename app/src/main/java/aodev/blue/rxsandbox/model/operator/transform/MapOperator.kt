package aodev.blue.rxsandbox.model.operator.transform

import aodev.blue.rxsandbox.model.Event
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operations.mapping.Mapping
import aodev.blue.rxsandbox.model.operator.Operator


class MapOperator<in T, out R>(private val mapping: Mapping<T, R>) : Operator<T, R> {

    override fun apply(timeline: Timeline<T>): Timeline<R> {
        return Timeline(
                timeline.events.map { Event(it.time, mapping.map(it.value)) }.toSet(),
                timeline.termination
        )
    }

    override fun expression(): String {
        return "map { ${mapping.expression()} }"
    }
}