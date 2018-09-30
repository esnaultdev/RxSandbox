package aodev.blue.rxsandbox.model.operator.observable.create

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.ParamsNone


class ObservableInterval(
        private val interval: Float
) : Operator<Unit, Int, ParamsNone, ObservableT<Int>> {

    init {
        require(interval > 0)
    }

    override fun params(input: List<Timeline<Unit>>): ParamsNone? {
        return ParamsNone.fromInput(input)
    }

    override fun apply(params: ParamsNone): ObservableT<Int> {
        return apply()
    }

    fun apply(): ObservableT<Int> {
        val count = (Config.timelineDuration / interval).toInt()
        val events = (0..count).map { ObservableT.Event(it * interval, it) }

        return ObservableT(
                events,
                ObservableT.Termination.None
        )
    }

    override fun expression(): String {
        return "interval(${"%.1f".format(interval)})"
    }
}