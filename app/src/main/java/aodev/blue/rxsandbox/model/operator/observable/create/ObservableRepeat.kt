package aodev.blue.rxsandbox.model.operator.observable.create

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.operator.Operator

// FIXME This limit is used to avoid a laggy UI when too many events gets generated.
// Find a better way to deal with this case
private const val EVENT_LIMIT = 100

class ObservableRepeat<T : Any> : Operator {

    fun apply(input: ObservableT<T>): ObservableT<T> {
        return when (input.termination) {
            ObservableT.Termination.None -> input
            is ObservableT.Termination.Error -> input
            is ObservableT.Termination.Complete -> {
                if (input.termination.time == 0f) {
                    ObservableT(emptyList(), ObservableT.Termination.None)
                } else {
                    val repeatTime = input.termination.time
                    val theoreticalRepeatCount = (Config.timelineDuration / repeatTime).toInt()
                    val pragmaticRepeatCount = minOf(theoreticalRepeatCount, EVENT_LIMIT)

                    val events = (0..pragmaticRepeatCount).map { repeatIndex ->
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