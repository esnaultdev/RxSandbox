package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.ParamsObservable


class ObservableElementAt<T>(
        private val index: Int
) : Operator<T, T, ParamsObservable<T>, SingleT<T>> {

    init {
        require(index >= 0)
    }

    override fun params(input: List<Timeline<T>>): ParamsObservable<T>? {
        return ParamsObservable.fromInput(input)
    }

    override fun apply(params: ParamsObservable<T>): SingleT<T> {
        return apply(params.observable)
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

    override fun expression(): String {
        return "elementAt($index)"
    }
}