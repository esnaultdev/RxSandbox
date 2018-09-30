package aodev.blue.rxsandbox.model.operator.observable.conditional

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operation.predicate.Predicate
import aodev.blue.rxsandbox.model.operator.ParamsObservable


class ObservableAny<T>(
        private val predicate: Predicate<T>
) : Operator<T, Boolean, ParamsObservable<T>, SingleT<Boolean>> {

    override fun params(input: List<Timeline<T>>): ParamsObservable<T>? {
        return ParamsObservable.fromInput(input)
    }

    override fun apply(params: ParamsObservable<T>): SingleT<Boolean> {
        return apply(params.observable)
    }

    fun apply(input: ObservableT<T>): SingleT<Boolean> {
        val firstGood = input.events.firstOrNull { predicate.check(it.value) }

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

    override fun expression(): String {
        return "any { ${predicate.expression()} }"
    }
}