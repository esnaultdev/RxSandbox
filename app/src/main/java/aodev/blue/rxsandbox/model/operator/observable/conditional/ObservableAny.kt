package aodev.blue.rxsandbox.model.operator.observable.conditional

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.functions.Predicate
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableAny<T : Any>(private val predicate: Predicate<T>) : Operator {

    fun apply(input: ObservableT<T>): SingleT<Boolean> {
        val firstGood = input.events.firstOrNull { predicate.test(it.value) }

        return if (firstGood != null) {
            SingleT(SingleT.Result.Success(firstGood.time, true))
        } else when (input.termination) {
            ObservableT.Termination.None -> SingleT(SingleT.Result.None())
            is ObservableT.Termination.Complete -> {
                SingleT(SingleT.Result.Success(input.termination.time, false))
            }
            is ObservableT.Termination.Error -> {
                SingleT(SingleT.Result.Error(input.termination.time))
            }
        }
    }

    override val expression: String = "any { ${predicate.expression} }"

    override val docUrl: String? = null
}