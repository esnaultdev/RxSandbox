package aodev.blue.rxsandbox.model.operator.observable.create

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Input
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableJust<T : Any>(private vararg val values: T) : Operator<T, T> {

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

    override val expression: String = "just(${values.joinToString(", ")})"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}just.html"
}