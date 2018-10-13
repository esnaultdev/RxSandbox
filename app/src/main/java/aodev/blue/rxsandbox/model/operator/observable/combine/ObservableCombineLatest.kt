package aodev.blue.rxsandbox.model.operator.observable.combine

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.functions.Function
import aodev.blue.rxsandbox.model.operator.Input
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.time
import aodev.blue.rxsandbox.utils.alter


class ObservableCombineLatest<T : Any, R : Any>(
        private val combiner: Function<List<T>, R>
) : Operator<T, R> {

    override fun apply(input: List<Timeline<T>>): Timeline<R>? {
        return Input.Observables.from(input) {
            apply(it)
        }
    }

    fun apply(input: List<ObservableT<T>>): ObservableT<R> {
        return if (input.isEmpty()) {
            ObservableT(emptyList(), ObservableT.Termination.None)
        } else {
            val terminations = input.map { it.termination }
            val firstError = terminations
                    .filterIsInstance<ObservableT.Termination.Error>()
                    .sortedBy { it.time }
                    .firstOrNull()

            val termination = when {
                firstError != null -> firstError
                terminations.all { it is ObservableT.Termination.Complete } -> {
                    terminations.map { it as ObservableT.Termination.Complete }
                            .sortedBy { it.time }
                            .last()
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

                getCombinedEvents(filteredEvents).map {
                    ObservableT.Event(it.first, combiner.apply(it.second))
                }
            }
            ObservableT(events, termination)
        }
    }

    private fun getCombinedEvents(
            events: List<List<ObservableT.Event<T>>>
    ): List<Pair<Float, List<T>>> {
        return events.mapIndexed { index, list -> list.map { index to it } }
                .flatten()
                .sortedWith(
                        compareBy<Pair<Int, ObservableT.Event<T>>> { it.second.time }
                                .thenBy { it.first }
                )
                .fold<Pair<Int, ObservableT.Event<T>>, List<Pair<Float, List<T?>>>>(
                        initial = listOf(0f to (0 until events.size).map { null })
                ) {
                    accumulator, eventWithIndex ->
                    val (index, event) = eventWithIndex
                    val lastValues = accumulator.last().second
                    val newValues = lastValues.alter(index, event.value)
                    accumulator + (event.time to newValues)
                }
                .dropWhile { it.second.any { item -> item == null } }
                .map {
                    @Suppress("UNCHECKED_CAST")
                    it as Pair<Float, List<T>>
                }
    }

    override val expression: String = "combineLatest { ${combiner.expression} }"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}combinelatest.html"
}