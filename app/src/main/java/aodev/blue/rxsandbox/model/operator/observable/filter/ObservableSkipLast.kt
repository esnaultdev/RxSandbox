package aodev.blue.rxsandbox.model.operator.observable.filter

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableSkipLast<T : Any>(private val count: Int) : Operator {

    init {
        require(count >= 0)
    }

    // TODO verify the behavior of the skipLast with an error

    fun apply(input: ObservableT<T>): ObservableT<T> {
        return when (input.termination) {
            ObservableT.Termination.None -> ObservableT(emptyList(), input.termination)
            is ObservableT.Termination.Error -> ObservableT(emptyList(), input.termination)
            is ObservableT.Termination.Complete -> {
                val events = input.events.dropLast(count)
                        .map { it.moveTo(input.termination.time) }
                ObservableT(events, input.termination)
            }
        }
    }

    override val expression: String = "skipLast($count)"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}skiplast.html"
}