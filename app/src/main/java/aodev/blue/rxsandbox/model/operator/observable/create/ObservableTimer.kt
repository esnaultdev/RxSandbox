package aodev.blue.rxsandbox.model.operator.observable.create

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.ParamsNone


class ObservableTimer(
        private val delay: Float
) : Operator<Unit, Int, ParamsNone, ObservableT<Int>> {

    init {
        require(delay >= 0)
    }

    override fun params(input: List<Timeline<Unit>>): ParamsNone? {
        return ParamsNone.fromInput(input)
    }

    override fun apply(params: ParamsNone): ObservableT<Int> {
        return apply()
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

    override fun expression(): String {
        return "timer(${"%.1f".format(delay)})"
    }
}