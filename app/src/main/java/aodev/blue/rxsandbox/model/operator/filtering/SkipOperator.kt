package aodev.blue.rxsandbox.model.operator.filtering

import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator


class SkipOperator<T>(private val count: Int) : Operator<T, T> {

    override fun apply(timeline: Timeline<T>): Timeline<T> {
        return Timeline(
                timeline.sortedEvents.drop(count).toSet(),
                timeline.termination
        )
    }

    override fun expression(): String {
        return "skip $count"
    }
}