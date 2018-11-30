package aodev.blue.rxsandbox.model.operator.observable.create

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableInterval<T : Any>(private val interval: Float) : Operator {

    init {
        require(interval > 0)
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