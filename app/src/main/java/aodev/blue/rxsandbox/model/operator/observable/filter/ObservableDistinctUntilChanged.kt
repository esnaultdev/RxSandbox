package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableDistinctUntilChanged<T : Any> : Operator {

    fun apply(input: ObservableT<T>): ObservableT<T> {
        val events = when (input.events.size) {
            0 -> emptyList()
            1 -> listOf(input.events.first())
            else -> {
                val firstEvent = input.events.first()
                val otherEvents = input.events.drop(1)
                        .zip(input.events)
                        .filter { it.first.value != it.second.value }
                        .map { it.first }

                mutableListOf(firstEvent).apply { addAll(otherEvents) }
            }
        }

        return ObservableT(
                events = events,
                termination = input.termination
        )
    }

    override val expression: String = "distinctUntilChanged"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}distinct.html"
}