package aodev.blue.rxsandbox.model.operator.observable.create

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableRange(private val from: Int, private val to: Int) : Operator {

    init {
        require(from >= 0)
        require(to >= 0)
    }

    fun apply(): ObservableT<Int> {
        return ObservableT(
                (from..to).map { ObservableT.Event(0f, it) },
                ObservableT.Termination.Complete(0f)
        )
    }

    override val expression: String = "range($from, $to)"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}range.html"
}