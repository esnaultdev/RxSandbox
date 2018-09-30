package aodev.blue.rxsandbox.model.operator.observable.create

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Input
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableJust<T>(private vararg val values: T) : Operator<T, T> {

    override fun apply(input: List<Timeline<T>>): Timeline<T>? {
        return Input.None.from(input) {
            apply()
        }
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