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
            val termination = getTermination(input)
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

    private fun getTermination(input: List<ObservableT<T>>): ObservableT.Termination {
        val terminations = input.map { it.termination }

        val firstError = terminations
                .filterIsInstance<ObservableT.Termination.Error>()
                .minBy { it.time }

        val completion = getCompletion(input)

        return when {
            firstError != null && completion != null -> {
                if (firstError.time <= completion.time) {
                    firstError
                } else {
                    completion
                }
            }
            firstError != null -> firstError
            completion != null -> completion
            else -> ObservableT.Termination.None
        }
    }

    /**
     * Get the completion of the operator result.
     * The zip operator completes when:
     * - All other sources have provided more events than the one that completes (based on the
     *   completion we now that we can't zip any new events from the other sources)
     *
     *   For example:
     *
     *   --O-------O----|---->
     *
     *   -----O--|----------->
     *
     *   ======== zip ========
     *
     *   -----O--|----------->
     *
     * or
     *
     * - All the other sources have already completed and the last source emits the last event
     *   that can be zipped with the other sources events.
     *
     *   For example:
     *
     *   --O---O---|--------->
     *
     *   -----O------O---O--->
     *
     *   ======== zip ========
     *
     *   -----O------O|------>
     */
    private fun getCompletion(input: List<ObservableT<T>>): ObservableT.Termination.Complete? {
        val fewestElementsTimeline = input
                .filter { it.termination is ObservableT.Termination.Complete }
                .sortedWith(
                        compareBy<ObservableT<T>> { it.events.size }
                                .thenBy { it.termination.time }
                )
                .firstOrNull()
                ?: return null

        val zipCount = fewestElementsTimeline.events.size
        val fewestElementComplete =
                fewestElementsTimeline.termination as ObservableT.Termination.Complete

        if (zipCount == 0) {
            // We don't have any element, just return the earliest completion
            return fewestElementComplete
        }

        val allHaveEnoughEvents = input.all { it.events.size >= zipCount }
        if (!allHaveEnoughEvents) {
            return null
        }

        // Find the time at which the latest event at index zipCount is emitted.
        val lastEventTimeForZipCount = input.map { it.events[zipCount - 1].time }.max()!!

        return if (lastEventTimeForZipCount > fewestElementComplete.time) {
            // Complete using the latest zipCount event time
            ObservableT.Termination.Complete(lastEventTimeForZipCount)
        } else {
            // Complete using the fewestElementsTimeline completion
            fewestElementComplete
        }
    }

    override val expression: String = "zip { ${zipper.expression} }"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}zip.html"
}
