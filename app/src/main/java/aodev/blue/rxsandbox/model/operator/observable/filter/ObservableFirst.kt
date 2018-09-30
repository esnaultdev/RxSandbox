package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Input
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableFirst<T> : Operator<T, T> {

    override fun apply(input: List<Timeline<T>>): Timeline<T>? {
        return Input.Observable.from(input) {
            apply(it)
        }
    }

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

    override fun expression(): String {
        return "first"
    }
}
