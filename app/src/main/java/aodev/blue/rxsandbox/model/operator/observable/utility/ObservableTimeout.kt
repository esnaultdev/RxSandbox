package aodev.blue.rxsandbox.model.operator.observable.utility

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Input
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.utils.toLinkedList

class ObservableTimeout<T : Any>(private val timeout: Float) : Operator<T, T> {

    init {
        require(timeout >= 0)
    }

    override fun apply(input: List<Timeline<T>>): Timeline<T>? {
        return Input.Observable.from(input, ::apply)
    }

    fun apply(input: ObservableT<T>): ObservableT<T> {
        val timeoutTime = input.events.map { it.time }
                .toLinkedList()
                .apply {
                    push(0f)
                    when (input.termination) {
                        is ObservableT.Termination.Error -> add(input.termination.time)
                        is ObservableT.Termination.Complete -> add(input.termination.time)
                        is ObservableT.Termination.None -> add(Config.timelineDuration.toFloat())
                    }
                }
                .asSequence()
                .zipWithNext()
                .firstOrNull{ (previous, next) -> next - previous > timeout }
                ?.first
                ?.let { it + timeout }
                ?.takeIf { it < Config.timelineDuration }

        return if (timeoutTime != null) {
            ObservableT(
                    input.events.filter { it.time < timeoutTime },
                    ObservableT.Termination.Error(timeoutTime)
            )
        } else {
            input
        }
    }

    override val expression: String = "timeout(${"%.1f".format(timeout)})"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}timeout.html"
}
