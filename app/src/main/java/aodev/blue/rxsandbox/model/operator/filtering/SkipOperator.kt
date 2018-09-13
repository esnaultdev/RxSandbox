package aodev.blue.rxsandbox.model.operator.filtering

import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator

/**
 * Created by matthieuesnault on 13/09/2018.
 */
class SkipOperator<T>(private val count: Int) : Operator<T, T> {

    override fun apply(timeline: Timeline<T>): Timeline<T> {
        return Timeline(
            timeline.events.drop(count),
            timeline.termination
        )
    }

    override fun expression(): String {
        return "skip $count"
    }
}