package aodev.blue.rxsandbox.model.operator.observable.combine

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.functions.Function
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.utils.zip


class ObservableZip<T : Any, R : Any>(
        private val zipper: Function<List<T>, R>
) : Operator {

    fun apply(input: List<ObservableT<T>>): ObservableT<R> {
        return if (input.isEmpty()) {
            ObservableT(emptyList(), ObservableT.Termination.None)
        } else {
            val terminations = input.map { it.termination }
            val firstError = terminations
                    .filterIsInstance<ObservableT.Termination.Error>()
                    .minBy { it.time }

            // FIXME this termination selection is not right,
            // when a completion occurs after fewer elements in its timeline
            // than an error, the completion must be selected
            // However, this completion might occur after fewer elements,
            // but still after the error of another stream.
            // The completion can also occur after the last waited element
            val termination = when {
                firstError != null -> firstError
                terminations.all { it is ObservableT.Termination.Complete } -> {
                    terminations.map { it as ObservableT.Termination.Complete }
                            .maxBy { it.time }!!
                }
                else -> ObservableT.Termination.None
            }
            val terminationTime = termination.time

            val inputEvents = input.map { it.events }
            val events: List<ObservableT.Event<R>>
            events = if (inputEvents.any { it.isEmpty() }) {
                emptyList()
            } else {
                val filteredEvents = if (terminationTime != null) {
                    inputEvents.map { it.filter { event -> event.time <= terminationTime } }
                } else {
                    inputEvents
                }

                zip(*filteredEvents.toTypedArray()) { toZip ->
                    val time = toZip.map { it.time }.max() ?: 0f
                    ObservableT.Event(time, zipper.apply(toZip.map { it.value }))
                }
            }
            ObservableT(events, termination)
        }
    }

    override val expression: String = "zip { ${zipper.expression} }"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}zip.html"
}
