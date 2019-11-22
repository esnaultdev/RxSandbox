package aodev.blue.rxsandbox.model.operator.observable.combine

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.functions.Function
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.utils.alter


class ObservableCombineLatest<T : Any, R : Any>(
        private val combiner: Function<List<T>, R>
) : Operator {

    fun apply(input: List<ObservableT<T>>): ObservableT<R> {
        return if (input.isEmpty()) {
            ObservableT(emptyList(), ObservableT.Termination.None)
        } else {
            val termination = getTermination(input)
            val events = getEvents(input, termination)
            ObservableT(events, termination)
        }
    }

    private fun getTermination(input: List<ObservableT<T>>): ObservableT.Termination {
        val terminations = input.map { it.termination }

        val firstError = terminations
                .filterIsInstance<ObservableT.Termination.Error>()
                .minBy { it.time }

        // If an input timeline completes without having any item, we can't combine anything since
        // we don't have any value, so we can complete right away.
        val firstEmptyComplete = terminations
                .filterIndexed { index, _ -> input[index].events.isEmpty() }
                .filterIsInstance<ObservableT.Termination.Complete>()
                .minBy { it.time }

        return when {
            firstError != null && firstEmptyComplete != null -> {
                if (firstError.time <= firstEmptyComplete.time) {
                    firstError
                } else {
                    firstEmptyComplete
                }
            }
            firstError != null -> firstError
            firstEmptyComplete != null -> firstEmptyComplete
            terminations.all { it is ObservableT.Termination.Complete } -> {
                terminations.map { it as ObservableT.Termination.Complete }
                        .maxBy { it.time }!!
            }
            else -> ObservableT.Termination.None
        }
    }

    private fun getEvents(
            input: List<ObservableT<T>>,
            termination: ObservableT.Termination
    ): List<ObservableT.Event<R>> {
        val terminationTime = termination.time
        val inputEvents = input.map { it.events }

        return if (inputEvents.any { it.isEmpty() }) {
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
    }

    private fun getCombinedEvents(
            events: List<List<ObservableT.Event<T>>>
    ): List<Pair<Float, List<T>>> {
        return events.asSequence()
                .mapIndexed { index, list -> list.map { index to it } }
                .flatten()
                .sortedWith(
                        compareBy<Pair<Int, ObservableT.Event<T>>> { it.second.time }
                                .thenBy { it.first }
                )
                // Here we have all events mixed together, associated with the index of their input
                // Let's fold to obtain a list with, at each event time, the list of all the last
                // values for each input index
                .fold<Pair<Int, ObservableT.Event<T>>, List<Pair<Float, List<T?>>>>(
                        initial = listOf(0f to events.indices.map { null })
                ) {
                    accumulator, eventWithIndex ->
                    val (index, event) = eventWithIndex
                    val lastValues = accumulator.last().second
                    val newValues = lastValues.alter(index, event.value)
                    accumulator + (event.time to newValues)
                }
                // The first lists do not have all their items, so we need to filter them out
                .dropWhile { it.second.any { item -> item == null } }
                // Since we've filtered the null values, let's cast every value to T instead of T?
                .map {
                    @Suppress("UNCHECKED_CAST")
                    it as Pair<Float, List<T>>
                }
    }

    override val expression: String = "combineLatest { ${combiner.expression} }"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}combinelatest.html"
}
