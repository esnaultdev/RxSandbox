package aodev.blue.rxsandbox.model.operator.observable.create

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.ParamsObservable


class ObservableRepeat<T> : Operator<T, T, ParamsObservable<T>, ObservableT<T>> {

    override fun params(input: List<Timeline<T>>): ParamsObservable<T>? {
        return ParamsObservable.fromInput(input)
    }

    override fun apply(params: ParamsObservable<T>): ObservableT<T> {
        return apply(params.observable)
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

    override fun expression(): String {
        return "repeat"
    }
}