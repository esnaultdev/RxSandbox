package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.ParamsObservable
import aodev.blue.rxsandbox.utils.clamp


class ObservableDebounce<T>(
        private val duration: Float
) : Operator<T, T, ParamsObservable<T>, ObservableT<T>> {

    // TODO Verify the behavior of the debounce with an error

    init {
        require(duration >= 0)
    }

    override fun params(input: List<Timeline<T>>): ParamsObservable<T>? {
        return ParamsObservable.fromInput(input)
    }

    override fun apply(params: ParamsObservable<T>): ObservableT<T> {
        return apply(params.observable)
    }

    fun apply(input: ObservableT<T>): ObservableT<T> {
        val firstEvents = input.events
                .zip(input.events.drop(1))
                .filter { (event, next) -> next.time - event.time >= duration }
                .map { (event, _) -> event.moveTo(event.time + duration) }

        val lastEvent: ObservableT.Event<T>? = input.events.lastOrNull()?.let {
            when (input.termination) {
                ObservableT.Termination.None -> {
                    val newTime = (it.time + duration).clamp(0f, Config.timelineDuration.toFloat())
                    it.moveTo(newTime)
                }
                is ObservableT.Termination.Complete -> {
                    val newTime = (it.time + duration).clamp(0f, input.termination.time)
                    it.moveTo(newTime)
                }
                is ObservableT.Termination.Error -> null
            }
        }

        val events = if (lastEvent != null) firstEvents + lastEvent else firstEvents

        return ObservableT(
                events,
                input.termination
        )
    }

    override fun expression(): String {
            return "debounce(${"%.1f".format(duration)})"
    }
}