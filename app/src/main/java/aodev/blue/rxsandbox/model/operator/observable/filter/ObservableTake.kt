package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableTake<T : Any>(private val count: Int) : Operator {

    init {
        require(count >= 0)
    }

    fun apply(input: ObservableT<T>): ObservableT<T> {
        return if (input.events.size >= count) {
            val events = input.events.take(count)
            ObservableT(
                    events,
                    ObservableT.Termination.Complete(events.lastOrNull()?.time ?: 0f)
            )
        } else {
            input
        }
    }

    override val expression: String = "take($count)"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}take.html"
}