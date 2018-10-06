package aodev.blue.rxsandbox.model.operator.observable.create

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Input
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableTimer<T : Any>(private val delay: Float) : Operator<T, Int> {

    init {
        require(delay >= 0)
    }

    override fun apply(input: List<Timeline<T>>): Timeline<Int>? {
        return Input.None.from(input) {
            apply()
        }
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