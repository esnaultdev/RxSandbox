package aodev.blue.rxsandbox.model.sample

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.ObservableT.Companion.eventsOf
import aodev.blue.rxsandbox.model.operator.observable.create.ObservableEmpty
import aodev.blue.rxsandbox.model.operator.observable.create.ObservableInterval
import aodev.blue.rxsandbox.model.operator.observable.create.ObservableJust
import aodev.blue.rxsandbox.model.operator.observable.create.ObservableNever
import aodev.blue.rxsandbox.model.operator.observable.create.ObservableRange
import aodev.blue.rxsandbox.model.operator.observable.create.ObservableRepeat
import aodev.blue.rxsandbox.model.operator.observable.create.ObservableThrow
import aodev.blue.rxsandbox.model.operator.observable.create.ObservableTimer
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableDebounce
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableDistinct
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableDistinctUntilChanged
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableFilter
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableFirst
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableIgnoreElements
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableLast
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableSkip
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableSkipLast
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableTake
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableTakeLast
import aodev.blue.rxsandbox.model.operator.observable.transform.ObservableMap
import aodev.blue.rxsandbox.model.operator.observable.utility.ObservableDelay
import aodev.blue.rxsandbox.model.operation.mapping.MultiplyMapping
import aodev.blue.rxsandbox.model.operation.predicate.EvenPredicate
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableElementAt


fun getObservableSample(operatorName: String): OperatorSample? {
    return when (operatorName) {
        // Create
        "empty" -> {
            OperatorSample(
                    input = emptyList(),
                    operator = ObservableEmpty()
            )
        }
        "interval" -> {
            OperatorSample(
                    input = emptyList(),
                    operator = ObservableInterval(2f)
            )
        }
        "just" -> {
            OperatorSample(
                    input = emptyList(),
                    operator = ObservableJust(1, 2, 3, 4, 5)
            )
        }
        "never" -> {
            OperatorSample(
                    input = emptyList(),
                    operator = ObservableNever()
            )
        }
        "range" -> {
            OperatorSample(
                    input = emptyList(),
                    operator = ObservableRange(1, 5)
            )
        }
        "repeat" -> {
            OperatorSample(
                    input = listOf(
                            ObservableT(
                                    events = eventsOf(
                                            0f to 1
                                    ),
                                    termination = ObservableT.Termination.Complete(2f)
                            )
                    ),
                    operator = ObservableRepeat<Int>()
            )
        }
        "throw" -> {
            OperatorSample(
                    input = emptyList(),
                    operator = ObservableThrow()
            )
        }
        "timer" -> {
            OperatorSample(
                    input = emptyList(),
                    operator = ObservableTimer(5f)
            )
        }

        // Transform
        "map" -> {
            OperatorSample(
                    input = listOf(
                            ObservableT(
                                    events = eventsOf(
                                            0f to 1,
                                            2f to 2,
                                            4f to 3,
                                            6f to 4,
                                            8f to 5
                                    ),
                                    termination = ObservableT.Termination.Complete(10f)
                            )
                    ),
                    operator = ObservableMap(MultiplyMapping(2))
            )
        }

        // Filter
        "debounce" -> {
            OperatorSample(
                    input = listOf(
                            ObservableT(
                                    events = eventsOf(
                                            0f to 1,
                                            4f to 2,
                                            6f to 3
                                    ),
                                    termination = ObservableT.Termination.Complete(10f)
                            )
                    ),
                    operator = ObservableDebounce(3f)
            )
        }
        "distinct" -> {
            OperatorSample(
                    input = listOf(
                            ObservableT(
                                    events = eventsOf(
                                            0f to 1,
                                            2f to 2,
                                            4f to 2,
                                            6f to 3,
                                            8f to 1
                                    ),
                                    termination = ObservableT.Termination.Complete(10f)
                            )
                    ),
                    operator = ObservableDistinct()
            )
        }
        "distinctUntilChanged" -> {
            OperatorSample(
                    input = listOf(
                            ObservableT(
                                    events = eventsOf(
                                            0f to 1,
                                            2f to 2,
                                            4f to 2,
                                            6f to 3,
                                            8f to 1
                                    ),
                                    termination = ObservableT.Termination.Complete(10f)
                            )
                    ),
                    operator = ObservableDistinctUntilChanged()
            )
        }
        "elementAt" -> {
            OperatorSample(
                    input = listOf(
                            ObservableT(
                                    events = eventsOf(
                                            0f to 1,
                                            2f to 2,
                                            4f to 2,
                                            6f to 3,
                                            8f to 1
                                    ),
                                    termination = ObservableT.Termination.Complete(10f)
                            )
                    ),
                    operator = ObservableElementAt(2)
            )
        }
        "filter" -> {
            OperatorSample(
                    input = listOf(
                            ObservableT(
                                    events = eventsOf(
                                            0f to 1,
                                            2f to 2,
                                            4f to 3,
                                            6f to 4,
                                            8f to 5
                                    ),
                                    termination = ObservableT.Termination.Complete(10f)
                            )
                    ),
                    operator = ObservableFilter(EvenPredicate())
            )
        }
        "first" -> {
            OperatorSample(
                    input = listOf(
                            ObservableT(
                                    events = eventsOf(
                                            0f to 1,
                                            2f to 2,
                                            4f to 3,
                                            6f to 4,
                                            8f to 5
                                    ),
                                    termination = ObservableT.Termination.Complete(10f)
                            )
                    ),
                    operator = ObservableFirst()
            )
        }
        "ignoreElements" -> {
            OperatorSample(
                    input = listOf(
                            ObservableT(
                                    events = eventsOf(
                                            0f to 1,
                                            2f to 2,
                                            4f to 3,
                                            6f to 4,
                                            8f to 5
                                    ),
                                    termination = ObservableT.Termination.Complete(10f)
                            )
                    ),
                    operator = ObservableIgnoreElements()
            )
        }
        "last" -> {
            OperatorSample(
                    input = listOf(
                            ObservableT(
                                    events = eventsOf(
                                            0f to 1,
                                            2f to 2,
                                            4f to 3,
                                            6f to 4,
                                            8f to 5
                                    ),
                                    termination = ObservableT.Termination.Complete(10f)
                            )
                    ),
                    operator = ObservableLast()
            )
        }
        "skip" -> {
            OperatorSample(
                    input = listOf(
                            ObservableT(
                                    events = eventsOf(
                                            0f to 1,
                                            2f to 2,
                                            4f to 3,
                                            6f to 4,
                                            8f to 5
                                    ),
                                    termination = ObservableT.Termination.Complete(10f)
                            )
                    ),
                    operator = ObservableSkip(2)
            )
        }
        "skipLast" -> {
            OperatorSample(
                    input = listOf(
                            ObservableT(
                                    events = eventsOf(
                                            0f to 1,
                                            2f to 2,
                                            4f to 3,
                                            6f to 4,
                                            8f to 5
                                    ),
                                    termination = ObservableT.Termination.Complete(10f)
                            )
                    ),
                    operator = ObservableSkipLast(2)
            )
        }
        "take" -> {
            OperatorSample(
                    input = listOf(
                            ObservableT(
                                    events = eventsOf(
                                            0f to 1,
                                            2f to 2,
                                            4f to 3,
                                            6f to 4,
                                            8f to 5
                                    ),
                                    termination = ObservableT.Termination.Complete(10f)
                            )
                    ),
                    operator = ObservableTake(2)
            )
        }
        "takeLast" -> {
            OperatorSample(
                    input = listOf(
                            ObservableT(
                                    events = eventsOf(
                                            0f to 1,
                                            2f to 2,
                                            4f to 3,
                                            6f to 4,
                                            8f to 5
                                    ),
                                    termination = ObservableT.Termination.Complete(10f)
                            )
                    ),
                    operator = ObservableTakeLast(2)
            )
        }
        // Utility
        "delay" -> {
            OperatorSample(
                    input = listOf(
                            ObservableT(
                                    events = eventsOf(
                                            0f to 1,
                                            2f to 2,
                                            4f to 3
                                    ),
                                    termination = ObservableT.Termination.Complete(6f)
                            )
                    ),
                    operator = ObservableDelay(2f)
            )
        }
        // Conditional
        /*
        // TODO Make these samples available when booleans are supported for display
        "all" -> {
            OperatorSample(
                    input = listOf(
                            ObservableT(
                                    events = eventsOf(
                                            0f to 1,
                                            2f to 2,
                                            4f to 3,
                                            6f to 4,
                                            8f to 5
                                    ),
                                    termination = ObservableT.Termination.Complete(10f)
                            )
                    ),
                    operator = ObservableAll(EvenPredicate())
            )
        }
        "any" -> {
            OperatorSample(
                    input = listOf(
                            ObservableT(
                                    events = eventsOf(
                                            0f to 1,
                                            2f to 2,
                                            4f to 3,
                                            6f to 4,
                                            8f to 5
                                    ),
                                    termination = ObservableT.Termination.Complete(10f)
                            )
                    ),
                    operator = ObservableAny(EvenPredicate())
            )
        }
        */
        else -> null
    }
}
