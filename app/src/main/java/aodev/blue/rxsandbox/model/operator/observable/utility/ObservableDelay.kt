package aodev.blue.rxsandbox.model.operator.observable.utility

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableDelay<T : Any>(private val delay: Float) : Operator {

    init {
        require(delay >= 0)
    }

    fun apply(input: ObservableT<T>): ObservableT<T> {
        val delayedEvents = input.events.mapNotNull { event ->
            val newTime = event.time + delay
            if (newTime <= Config.timelineDuration) {
                event.moveTo(newTime)
            } else {
                null
            }
        }

        val delayedTermination = when (input.termination) {
            ObservableT.Termination.None -> ObservableT.Termination.None
            is ObservableT.Termination.Error -> {
                val newTime = input.termination.time + delay
                if (newTime <= Config.timelineDuration) {
                    ObservableT.Termination.Error(newTime)
                } else {
                    ObservableT.Termination.None
                }
            }
            is ObservableT.Termination.Complete -> {
                val newTime = input.termination.time + delay
                if (newTime <= Config.timelineDuration) {
                    ObservableT.Termination.Complete(newTime)
                } else {
                    ObservableT.Termination.None
                }
            }
        }

        return ObservableT(delayedEvents, delayedTermination)
    }

    override val expression: String = "delay(${"%.1f".format(delay)})"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}delay.html"
}