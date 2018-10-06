package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Input
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableDistinct<T : Any> : Operator<T, T> {

    override fun apply(input: List<Timeline<T>>): Timeline<T>? {
        return Input.Observable.from(input) {
            apply(it)
        }
    }

    fun apply(input: ObservableT<T>): ObservableT<T> {
        val events = input.events.distinctBy { it.value }

        return ObservableT(
                events = events,
                termination = input.termination
        )
    }

    override val expression: String = "distinct"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}distinct.html"
}