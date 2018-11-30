package aodev.blue.rxsandbox.model.operator.observable.create

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableTimer(private val delay: Float) : Operator {

    init {
        require(delay >= 0)
    }

    fun apply(): ObservableT<Int> {
        return if (delay > Config.timelineDuration) {
            ObservableT(emptyList(), ObservableT.Termination.None)
        } else {
            ObservableT(
                    listOf(ObservableT.Event(delay, 0)),
                    ObservableT.Termination.Complete(delay)
            )
        }
    }

    override val expression: String = "timer(${"%.1f".format(delay)})"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}timer.html"
}