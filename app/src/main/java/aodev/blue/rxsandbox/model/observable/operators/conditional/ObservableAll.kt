package aodev.blue.rxsandbox.model.observable.operators.conditional

import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline
import aodev.blue.rxsandbox.model.operations.predicate.Predicate
import aodev.blue.rxsandbox.model.single.SingleResult
import aodev.blue.rxsandbox.model.single.SingleTimeline


class ObservableAll<T>(
        private val predicate: Predicate<T>
) : Operator<ObservableTimeline<T>, SingleTimeline<Boolean>> {

    override fun apply(input: ObservableTimeline<T>): SingleTimeline<Boolean> {
        val firstNot = input.events.firstOrNull { !predicate.check(it.value) }

        return if (firstNot != null) {
            SingleTimeline(SingleResult.Success(firstNot.time, false))
        } else when (input.termination) {
            ObservableTermination.None -> SingleTimeline(SingleResult.None())
            is ObservableTermination.Complete -> {
                SingleTimeline(SingleResult.Success(input.termination.time, true))
            }
            is ObservableTermination.Error -> {
                SingleTimeline(SingleResult.Error(input.termination.time))
            }
        }
    }

    override fun expression(): String {
        return "all { ${predicate.expression()} }"
    }
}