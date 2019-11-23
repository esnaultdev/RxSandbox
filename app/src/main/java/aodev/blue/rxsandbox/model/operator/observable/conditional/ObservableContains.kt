package aodev.blue.rxsandbox.model.operator.observable.conditional

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableContains<T : Any>(private val element: T) : Operator {

    fun apply(input: ObservableT<T>): SingleT<Boolean> {
        val event = input.events.firstOrNull { it.value == element }

        return if (event != null) {
            SingleT(SingleT.Result.Success(event.time, true))
        } else when (input.termination) {
            ObservableT.Termination.None -> SingleT<Boolean>(SingleT.Result.None())
            is ObservableT.Termination.Complete -> {
                SingleT(SingleT.Result.Success(input.termination.time, false))
            }
            is ObservableT.Termination.Error -> {
                SingleT(SingleT.Result.Error<Boolean>(input.termination.time))
            }
        }
    }

    // TODO improve the model and display of the operators to display this element
    override val expression: String = "contains($element)"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}contains.html"
}
