package aodev.blue.rxsandbox.model.operator.observable.create

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Input
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableRepeat<T : Any> : Operator<T, T> {

    override fun apply(input: List<Timeline<T>>): Timeline<T>? {
        return Input.Observable.from(input) {
            apply(it)
        }
    }

    fun apply(input: ObservableT<T>): ObservableT<T> {
        return when (input.termination) {
            ObservableT.Termination.None -> input
            is ObservableT.Termination.Error -> input
            is ObservableT.Termination.Complete -> {
                if (input.termination.time == 0f) {
                    ObservableT(emptyList(), ObservableT.Termination.None)
                } else {
                    val repeatTime = input.termination.time
                    val repeatCount = (Config.timelineDuration / repeatTime).toInt()
                    val events = (0..repeatCount).map { repeatIndex ->
                        input.events.mapNotNull { event ->
                            val newTime = event.time + repeatIndex * repeatTime
                            if (newTime <= Config.timelineDuration) {
                                ObservableT.Event(newTime, event.value)
                            } else {
                                null
                            }
                        }
                    }.flatten()

                    ObservableT(
                        events,
                        ObservableT.Termination.None
                    )
                }
            }
        }
    }

    override val expression: String = "repeat"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}repeat.html"
}