package aodev.blue.rxsandbox.model.operator.observable.create

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.ParamsNone


class ObservableRange(
        private val from: Int,
        private val to: Int
) : Operator<Unit, Int, ParamsNone, ObservableT<Int>> {

    init {
        require(from >= 0)
        require(to >= 0)
    }

    override fun params(input: List<Timeline<Unit>>): ParamsNone? {
        return ParamsNone.fromInput(input)
    }

    override fun apply(params: ParamsNone): ObservableT<Int> {
        return apply()
    }

    fun apply(): ObservableT<Int> {
        return ObservableT(
                (from..to).map { ObservableT.Event(0f, it) },
                ObservableT.Termination.Complete(0f)
        )
    }

    override fun expression(): String {
        return "range($from, $to)"
    }
}