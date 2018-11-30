package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableElementAt<T : Any>(private val index: Int) : Operator {

    init {
        require(index >= 0)
    }

    fun apply(input: ObservableT<T>): SingleT<T> {
        return when (input.termination) {
            is ObservableT.Termination.None -> SingleT(SingleT.Result.None())
            is ObservableT.Termination.Complete -> {
                if (input.events.size > index) {
                    val event = input.events[index]
                    SingleT(SingleT.Result.Success(event.time, event.value))
                } else {
                    SingleT(SingleT.Result.Error(input.termination.time))
                }
            }
            is ObservableT.Termination.Error -> {
                SingleT(SingleT.Result.Error(input.termination.time))
            }
        }
    }

    override val expression: String = "elementAt($index)"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}elementat.html"
}