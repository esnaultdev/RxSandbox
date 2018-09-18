package aodev.blue.rxsandbox.model.observable.operators.filter

import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline
import aodev.blue.rxsandbox.model.single.SingleResult
import aodev.blue.rxsandbox.model.single.SingleTimeline


class ObservableElementAt<T>(
        private val index: Int
) : Operator<ObservableTimeline<T>, SingleTimeline<T>> {

    init {
        require(index >= 0)
    }

    override fun apply(input: ObservableTimeline<T>): SingleTimeline<T> {
        return when (input.termination) {
            is ObservableTermination.None -> SingleTimeline(SingleResult.None())
            is ObservableTermination.Complete -> {
                if (input.events.size > index) {
                    val event = input.events[index]
                    SingleTimeline(SingleResult.Success(event.time, event.value))
                } else {
                    SingleTimeline(SingleResult.Error(input.termination.time))
                }
            }
            is ObservableTermination.Error -> {
                SingleTimeline(SingleResult.Error(input.termination.time))
            }
        }
    }

    override fun expression(): String {
        return "elementAt($index)"
    }
}