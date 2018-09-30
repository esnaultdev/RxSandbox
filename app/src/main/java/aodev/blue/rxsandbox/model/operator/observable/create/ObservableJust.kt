package aodev.blue.rxsandbox.model.operator.observable.create

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.ParamsNone


class ObservableJust<T>(
        private vararg val values: T
) : Operator<Unit, T, ParamsNone, ObservableT<T>> {

    override fun params(input: List<Timeline<Unit>>): ParamsNone? {
        return ParamsNone.fromInput(input)
    }

    override fun apply(params: ParamsNone): ObservableT<T> {
        return apply()
    }

    fun apply(): ObservableT<T> {
        return ObservableT(
                values.map { ObservableT.Event(0f, it) },
                ObservableT.Termination.Complete(0f)
        )
    }

    override fun expression(): String {
        return "just(${values.joinToString(", ")})"
    }
}