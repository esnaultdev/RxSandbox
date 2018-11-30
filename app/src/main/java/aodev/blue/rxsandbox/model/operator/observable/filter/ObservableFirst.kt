package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableFirst<T : Any> : Operator {

    fun apply(input: ObservableT<T>): SingleT<T> {
        return if (input.events.isNotEmpty()) {
            val event = input.events.first()
            SingleT(SingleT.Result.Success(event.time, event.value))
        } else {
            when (input.termination) {
                is ObservableT.Termination.None -> SingleT(SingleT.Result.None())
                is ObservableT.Termination.Complete -> {
                    SingleT(SingleT.Result.Error(input.termination.time))
                }
                is ObservableT.Termination.Error -> {
                    SingleT(SingleT.Result.Error(input.termination.time))
                }
            }
        }
    }

    override val expression: String = "first"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}first.html"
}
