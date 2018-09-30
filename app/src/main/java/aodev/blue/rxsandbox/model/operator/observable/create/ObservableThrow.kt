package aodev.blue.rxsandbox.model.operator.observable.create

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.ParamsNone


class ObservableThrow : Operator<Unit, Unit, ParamsNone, ObservableT<Unit>> {

    override fun params(input: List<Timeline<Unit>>): ParamsNone? {
        return ParamsNone.fromInput(input)
    }

    override fun apply(params: ParamsNone): ObservableT<Unit> {
        return apply()
    }

    fun apply(): ObservableT<Unit> {
        return ObservableT(
                emptyList(),
                ObservableT.Termination.Error(0f)
        )
    }

    override fun expression(): String {
        return "throw"
    }
}
