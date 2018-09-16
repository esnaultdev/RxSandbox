package aodev.blue.rxsandbox.model.observable.operators.filtering

import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline
import aodev.blue.rxsandbox.model.single.SingleResult
import aodev.blue.rxsandbox.model.single.SingleTimeline


class ObservableFirst<T> : Operator<ObservableTimeline<T>, SingleTimeline<T>> {

    override fun apply(input: ObservableTimeline<T>): SingleTimeline<T> {
        return if (input.events.isNotEmpty()) {
            val event = input.sortedEvents.first()
            SingleTimeline(SingleResult.Success(event.time, event.value))
        } else {
            when (input.termination) {
                is ObservableTermination.None -> SingleTimeline(SingleResult.None())
                is ObservableTermination.Complete -> {
                    SingleTimeline(SingleResult.Error(input.termination.time))
                }
                is ObservableTermination.Error -> {
                    SingleTimeline(SingleResult.Error(input.termination.time))
                }
            }
        }
    }

    override fun expression(): String {
        return "first"
    }
}
