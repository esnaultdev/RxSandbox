package aodev.blue.rxsandbox.model.operator.observable.conditional

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Input
import aodev.blue.rxsandbox.model.operator.Operator

class ObservableAmb<T : Any> : Operator<T, T> {

    override fun apply(input: List<Timeline<T>>): Timeline<T>? {
        return Input.Observables.from(input, ::apply)
    }

    fun apply(input: List<ObservableT<T>>): ObservableT<T> {
        val firstEmitTimes = input.map { timeline ->
            timeline.events.firstOrNull()?.time ?: when (timeline.termination) {
                is ObservableT.Termination.None -> null
                is ObservableT.Termination.Complete -> timeline.termination.time
                is ObservableT.Termination.Error -> timeline.termination.time
            }
        }

        return firstEmitTimes.withIndex()
                .filter { it.value != null }
                .sortedBy { it.value!! }
                .firstOrNull()
                ?.let { input[it.index] }
                ?: ObservableT(emptyList(), ObservableT.Termination.None)
    }

    override val expression: String = "amb"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}amb.html"
}