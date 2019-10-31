package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableSkip<T : Any>(private val count: Int) : Operator {

    init {
        require(count >= 0)
    }

    fun apply(input: ObservableT<T>): ObservableT<T> {
        return ObservableT(
                input.events.drop(count),
                input.termination
        )
    }

    override val expression: String = "skip($count)"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}skip.html"
}