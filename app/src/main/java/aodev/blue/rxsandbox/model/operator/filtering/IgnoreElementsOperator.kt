package aodev.blue.rxsandbox.model.operator.filtering

import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator


// FIXME This should return a Completable
class IgnoreElementsOperator<T> : Operator<T, T> {

    override fun apply(timeline: Timeline<T>): Timeline<T> {
        return Timeline(
                emptySet(),
                timeline.termination
        )
    }

    override fun expression(): String {
        return "ignoreElements"
    }
}