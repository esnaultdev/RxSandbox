package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Input
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableTakeLast<T>(private val count: Int) : Operator<T, T> {

    init {
        require(count >= 0)
    }

    // TODO verify the behavior of the takeLast with an error

    override fun apply(input: List<Timeline<T>>): Timeline<T>? {
        return Input.Observable.from(input) {
            apply(it)
        }
    }

    fun apply(input: ObservableT<T>): ObservableT<T> {
        return when (input.termination) {
            ObservableT.Termination.None -> ObservableT(emptyList(), input.termination)
            is ObservableT.Termination.Error -> ObservableT(emptyList(), input.termination)
            is ObservableT.Termination.Complete -> {
                val events = input.events.takeLast(count)
                        .map { it.moveTo(input.termination.time) }
                ObservableT(events, input.termination)
            }
        }
    }

    override fun expression(): String {
        return "takeLast($count)"
    }
}