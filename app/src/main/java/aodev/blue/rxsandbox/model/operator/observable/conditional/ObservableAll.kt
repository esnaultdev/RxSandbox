package aodev.blue.rxsandbox.model.operator.observable.conditional

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.functions.Predicate
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.Input


class ObservableAll<T : Any>(private val predicate: Predicate<T>) : Operator<T, Boolean> {

    override fun apply(input: List<Timeline<T>>): Timeline<Boolean>? {
        return Input.Observable.from(input) {
            apply(it)
        }
    }

    fun apply(input: ObservableT<T>): SingleT<Boolean> {
        val firstNot = input.events.firstOrNull { !predicate.test(it.value) }

        return if (firstNot != null) {
            SingleT(SingleT.Result.Success(firstNot.time, false))
        } else when (input.termination) {
            ObservableT.Termination.None -> SingleT(SingleT.Result.None())
            is ObservableT.Termination.Complete -> {
                SingleT(SingleT.Result.Success(input.termination.time, true))
            }
            is ObservableT.Termination.Error -> {
                SingleT(SingleT.Result.Error(input.termination.time))
            }
        }
    }

    override val expression: String = "all { ${predicate.expression} }"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}all.html"
}