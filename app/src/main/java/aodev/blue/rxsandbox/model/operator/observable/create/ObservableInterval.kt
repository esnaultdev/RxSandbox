package aodev.blue.rxsandbox.model.operator.observable.create

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Input
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableInterval<T : Any>(private val interval: Float) : Operator<T, Int> {

    init {
        require(interval > 0)
    }

    override fun apply(input: List<Timeline<T>>): Timeline<Int>? {
        return Input.None.from(input) {
            apply()
        }
    }

    fun apply(): ObservableT<Int> {
        val count = (Config.timelineDuration / interval).toInt()
        val events = (0..count).map { ObservableT.Event(it * interval, it) }

        return ObservableT(
                events,
                ObservableT.Termination.None
        )
    }

    override val expression: String = "interval(${"%.1f".format(interval)})"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}interval.html"
}