package aodev.blue.rxsandbox.model.operator.observable.utility

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Input
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableDelay<T>(private val delay: Float) : Operator<T, T> {

    init {
        require(delay >= 0)
    }

    override fun apply(input: List<Timeline<T>>): Timeline<T>? {
        return Input.Observable.from(input) {
            apply(it)
        }
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

    override fun expression(): String {
        return "delay(${"%.1f".format(delay)})"
    }
}