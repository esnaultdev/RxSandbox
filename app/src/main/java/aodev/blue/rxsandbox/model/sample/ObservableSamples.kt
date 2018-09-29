package aodev.blue.rxsandbox.model.sample

import aodev.blue.rxsandbox.model.observable.ObservableEvent
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline
import aodev.blue.rxsandbox.model.observable.operators.conditional.ObservableAll
import aodev.blue.rxsandbox.model.observable.operators.conditional.ObservableAny
import aodev.blue.rxsandbox.model.observable.operators.create.ObservableEmpty
import aodev.blue.rxsandbox.model.observable.operators.create.ObservableInterval
import aodev.blue.rxsandbox.model.observable.operators.create.ObservableJust
import aodev.blue.rxsandbox.model.observable.operators.create.ObservableNever
import aodev.blue.rxsandbox.model.observable.operators.create.ObservableRange
import aodev.blue.rxsandbox.model.observable.operators.create.ObservableRepeat
import aodev.blue.rxsandbox.model.observable.operators.create.ObservableThrow
import aodev.blue.rxsandbox.model.observable.operators.create.ObservableTimer
import aodev.blue.rxsandbox.model.observable.operators.filter.ObservableDebounce
import aodev.blue.rxsandbox.model.observable.operators.filter.ObservableDistinct
import aodev.blue.rxsandbox.model.observable.operators.filter.ObservableDistinctUntilChanged
import aodev.blue.rxsandbox.model.observable.operators.filter.ObservableFilter
import aodev.blue.rxsandbox.model.observable.operators.filter.ObservableFirst
import aodev.blue.rxsandbox.model.observable.operators.filter.ObservableIgnoreElements
import aodev.blue.rxsandbox.model.observable.operators.filter.ObservableLast
import aodev.blue.rxsandbox.model.observable.operators.filter.ObservableSkip
import aodev.blue.rxsandbox.model.observable.operators.filter.ObservableSkipLast
import aodev.blue.rxsandbox.model.observable.operators.filter.ObservableTake
import aodev.blue.rxsandbox.model.observable.operators.filter.ObservableTakeLast
import aodev.blue.rxsandbox.model.observable.operators.transform.ObservableMap
import aodev.blue.rxsandbox.model.observable.operators.utility.ObservableDelay
import aodev.blue.rxsandbox.model.operations.mapping.MultiplyMapping
import aodev.blue.rxsandbox.model.operations.predicate.EvenPredicate
import java.lang.IllegalArgumentException


fun <T, R> getObservableSample(operatorName: String): OperatorSample<T, R> {
    return when (operatorName) {
        // Create
        "empty" -> {
            OperatorSample(
                    input = Unit,
                    operator = ObservableEmpty<Int>()
            )
        }
        "interval" -> {
            OperatorSample(
                    input = Unit,
                    operator = ObservableInterval(2f)
            )
        }
        "just" -> {
            OperatorSample(
                    input = Unit,
                    operator = ObservableJust(1, 2, 3, 4, 5)
            )
        }
        "never" -> {
            OperatorSample(
                    input = Unit,
                    operator = ObservableNever<Int>()
            )
        }
        "range" -> {
            OperatorSample(
                    input = Unit,
                    operator = ObservableRange(1, 5)
            )
        }
        "repeat" -> {
            OperatorSample(
                    input = ObservableTimeline(
                            events = listOf(
                                    ObservableEvent(0f, 1)
                            ),
                            termination = ObservableTermination.Complete(2f)
                    ),
                    operator = ObservableRepeat()
            )
        }
        "throw" -> {
            OperatorSample(
                    input = Unit,
                    operator = ObservableThrow<Int>()
            )
        }
        "timer" -> {
            OperatorSample(
                    input = Unit,
                    operator = ObservableTimer(5f)
            )
        }

        // Transform
        "map" -> {
            OperatorSample(
                    input = ObservableTimeline(
                            events = listOf(
                                    ObservableEvent(0f, 1),
                                    ObservableEvent(2f, 2),
                                    ObservableEvent(4f, 3),
                                    ObservableEvent(6f, 4),
                                    ObservableEvent(8f, 5)
                            ),
                            termination = ObservableTermination.Complete(10f)
                    ),
                    operator = ObservableMap(MultiplyMapping(2))
            )
        }

        // Filter
        "debounce" -> {
            OperatorSample(
                    input = ObservableTimeline(
                            events = listOf(
                                    ObservableEvent(0f, 1),
                                    ObservableEvent(4f, 2),
                                    ObservableEvent(6f, 3)
                            ),
                            termination = ObservableTermination.Complete(10f)
                    ),
                    operator = ObservableDebounce(3f)
            )
        }
        "distinct" -> {
            OperatorSample(
                    input = ObservableTimeline(
                            events = listOf(
                                    ObservableEvent(0f, 1),
                                    ObservableEvent(2f, 2),
                                    ObservableEvent(4f, 2),
                                    ObservableEvent(6f, 3),
                                    ObservableEvent(8f, 1)
                            ),
                            termination = ObservableTermination.Complete(10f)
                    ),
                    operator = ObservableDistinct()
            )
        }
        "distinctUntilChanged" -> {
            OperatorSample(
                    input = ObservableTimeline(
                            events = listOf(
                                    ObservableEvent(0f, 1),
                                    ObservableEvent(2f, 2),
                                    ObservableEvent(4f, 2),
                                    ObservableEvent(6f, 3),
                                    ObservableEvent(8f, 1)
                            ),
                            termination = ObservableTermination.Complete(10f)
                    ),
                    operator = ObservableDistinctUntilChanged()
            )
        }
        "elementAt" -> {
            OperatorSample(
                    input = ObservableTimeline(
                            events = listOf(
                                    ObservableEvent(0f, 1),
                                    ObservableEvent(2f, 2),
                                    ObservableEvent(4f, 2),
                                    ObservableEvent(6f, 3),
                                    ObservableEvent(8f, 1)
                            ),
                            termination = ObservableTermination.Complete(10f)
                    ),
                    operator = ObservableDistinctUntilChanged()
            )
        }
        "filter" -> {
            OperatorSample(
                    input = ObservableTimeline(
                            events = listOf(
                                    ObservableEvent(0f, 1),
                                    ObservableEvent(2f, 2),
                                    ObservableEvent(4f, 3),
                                    ObservableEvent(6f, 4),
                                    ObservableEvent(8f, 5)
                            ),
                            termination = ObservableTermination.Complete(10f)
                    ),
                    operator = ObservableFilter(EvenPredicate())
            )
        }
        "first" -> {
            OperatorSample(
                    input = ObservableTimeline(
                            events = listOf(
                                    ObservableEvent(0f, 1),
                                    ObservableEvent(2f, 2),
                                    ObservableEvent(4f, 3),
                                    ObservableEvent(6f, 4),
                                    ObservableEvent(8f, 5)
                            ),
                            termination = ObservableTermination.Complete(10f)
                    ),
                    operator = ObservableFirst()
            )
        }
        "ignoreElements" -> {
            OperatorSample(
                    input = ObservableTimeline(
                            events = listOf(
                                    ObservableEvent(0f, 1),
                                    ObservableEvent(2f, 2),
                                    ObservableEvent(4f, 3),
                                    ObservableEvent(6f, 4),
                                    ObservableEvent(8f, 5)
                            ),
                            termination = ObservableTermination.Complete(10f)
                    ),
                    operator = ObservableIgnoreElements()
            )
        }
        "last" -> {
            OperatorSample(
                    input = ObservableTimeline(
                            events = listOf(
                                    ObservableEvent(0f, 1),
                                    ObservableEvent(2f, 2),
                                    ObservableEvent(4f, 3),
                                    ObservableEvent(6f, 4),
                                    ObservableEvent(8f, 5)
                            ),
                            termination = ObservableTermination.Complete(10f)
                    ),
                    operator = ObservableLast()
            )
        }
        "skip" -> {
            OperatorSample(
                    input = ObservableTimeline(
                            events = listOf(
                                    ObservableEvent(0f, 1),
                                    ObservableEvent(2f, 2),
                                    ObservableEvent(4f, 3),
                                    ObservableEvent(6f, 4),
                                    ObservableEvent(8f, 5)
                            ),
                            termination = ObservableTermination.Complete(10f)
                    ),
                    operator = ObservableSkip(2)
            )
        }
        "skipLast" -> {
            OperatorSample(
                    input = ObservableTimeline(
                            events = listOf(
                                    ObservableEvent(0f, 1),
                                    ObservableEvent(2f, 2),
                                    ObservableEvent(4f, 3),
                                    ObservableEvent(6f, 4),
                                    ObservableEvent(8f, 5)
                            ),
                            termination = ObservableTermination.Complete(10f)
                    ),
                    operator = ObservableSkipLast(2)
            )
        }
        "take" -> {
            OperatorSample(
                    input = ObservableTimeline(
                            events = listOf(
                                    ObservableEvent(0f, 1),
                                    ObservableEvent(2f, 2),
                                    ObservableEvent(4f, 3),
                                    ObservableEvent(6f, 4),
                                    ObservableEvent(8f, 5)
                            ),
                            termination = ObservableTermination.Complete(10f)
                    ),
                    operator = ObservableTake(2)
            )
        }
        "takeLast" -> {
            OperatorSample(
                    input = ObservableTimeline(
                            events = listOf(
                                    ObservableEvent(0f, 1),
                                    ObservableEvent(2f, 2),
                                    ObservableEvent(4f, 3),
                                    ObservableEvent(6f, 4),
                                    ObservableEvent(8f, 5)
                            ),
                            termination = ObservableTermination.Complete(10f)
                    ),
                    operator = ObservableTakeLast(2)
            )
        }
        // Utility
        "delay" -> {
            OperatorSample(
                    input = ObservableTimeline(
                            events = listOf(
                                    ObservableEvent(0f, 1),
                                    ObservableEvent(2f, 2),
                                    ObservableEvent(4f, 3)
                            ),
                            termination = ObservableTermination.Complete(6f)
                    ),
                    operator = ObservableDelay(2f)
            )
        }
        // Conditional
        "all" -> {
            OperatorSample(
                    input = ObservableTimeline(
                            events = listOf(
                                    ObservableEvent(0f, 1),
                                    ObservableEvent(2f, 2),
                                    ObservableEvent(4f, 3),
                                    ObservableEvent(6f, 4),
                                    ObservableEvent(8f, 5)
                            ),
                            termination = ObservableTermination.Complete(10f)
                    ),
                    operator = ObservableAll(EvenPredicate())
            )
        }
        "any" -> {
            OperatorSample(
                    input = ObservableTimeline(
                            events = listOf(
                                    ObservableEvent(0f, 1),
                                    ObservableEvent(2f, 2),
                                    ObservableEvent(4f, 3),
                                    ObservableEvent(6f, 4),
                                    ObservableEvent(8f, 5)
                            ),
                            termination = ObservableTermination.Complete(10f)
                    ),
                    operator = ObservableAny(EvenPredicate())
            )
        }
        else -> throw IllegalArgumentException("No observable operator for name $operatorName")
    } as OperatorSample<T, R>
}
