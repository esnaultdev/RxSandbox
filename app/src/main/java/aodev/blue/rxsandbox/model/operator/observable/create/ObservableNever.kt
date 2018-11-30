package aodev.blue.rxsandbox.model.operator.observable.create

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableNever<T : Any> : Operator {

    fun apply(): ObservableT<T> {
        return ObservableT(
                emptyList(),
                ObservableT.Termination.None
        )
    }

    override val expression: String = "never"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}empty-never-throw.html"
}