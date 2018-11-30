package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.CompletableT
import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableIgnoreElements<T : Any> : Operator {

    fun apply(input: ObservableT<T>): CompletableT {
        val result = when (input.termination) {
            ObservableT.Termination.None -> CompletableT.Result.None
            is ObservableT.Termination.Error -> CompletableT.Result.Error(input.termination.time)
            is ObservableT.Termination.Complete -> CompletableT.Result.Complete(input.termination.time)
        }

        return CompletableT(result)
    }

    override val expression: String = "ignoreElements"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}ignoreelements.html"
}