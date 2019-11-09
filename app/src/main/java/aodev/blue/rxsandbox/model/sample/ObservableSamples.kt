package aodev.blue.rxsandbox.model.sample

import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.ObservableX
import aodev.blue.rxsandbox.model.ReactiveTypeX
import aodev.blue.rxsandbox.model.functions.functionOf
import aodev.blue.rxsandbox.model.functions.predicateOf
import aodev.blue.rxsandbox.model.operator.observable.empty
import aodev.blue.rxsandbox.model.operator.observable.*


private val evenPredicate = predicateOf<Int>("x -> x % 2 == 0") { it % 2 == 0 }


fun getObservableSample(operatorName: String): ReactiveTypeX<*, *>? {
    return when (operatorName) {
        // Create
        "empty" -> ObservableX.empty<Int>()
        "interval" -> ObservableX.interval(2f)
        "just" -> ObservableX.just(1)
        "never" -> ObservableX.never<Int>()
        "range" -> ObservableX.range(1, 5)
        "repeat" -> {
            ObservableX.inputOf(
                    listOf(0f to 1),
                    ObservableT.Termination.Complete(2f)
            )
                    .repeat()
        }
        "throw" -> ObservableX.error<Int>()
        "timer" -> ObservableX.timer(5f)

        // Transform
        "map" -> {
            ObservableX.inputOf(
                    listOf(
                           0f to 1,
                           2f to 2,
                           4f to 3,
                           6f to 4,
                           8f to 5
                    ),
                    ObservableT.Termination.Complete(10f)
            )
                    .map<Int, Int>(functionOf("x -> x * 2") { x -> x *2 })
        }
        "scan" -> {
            ObservableX.inputOf(
                    listOf(
                            2f to 1,
                            4f to 2,
                            6f to 3,
                            8f to 4,
                            10f to 5
                    ),
                    ObservableT.Termination.Complete(10f)
            )
                    .scan(
                            0,
                            functionOf("acc, x -> acc + x") { acc, x -> acc + x }
                    )
        }

        // Filter
        "debounce" -> {
            ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            4f to 2,
                            6f to 3
                    ),
                    ObservableT.Termination.Complete(10f)
            )
                    .debounce(3f)
        }
        "distinct" -> {
            ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            2f to 2,
                            4f to 2,
                            6f to 3,
                            8f to 1
                    ),
                    ObservableT.Termination.Complete(10f)
            )
                    .distinct()
        }
        "distinctUntilChanged" -> {
            ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            2f to 2,
                            4f to 2,
                            6f to 3,
                            8f to 1
                    ),
                    ObservableT.Termination.Complete(10f)
            )
                    .distinctUntilChanged()
        }
        "elementAt" -> {
            ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            2f to 2,
                            4f to 2,
                            6f to 3,
                            8f to 1
                    ),
                    ObservableT.Termination.Complete(10f)
            )
                    .elementAt(2)
        }
        "filter" -> {
            ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            2f to 2,
                            4f to 3,
                            6f to 4,
                            8f to 5
                    ),
                    ObservableT.Termination.Complete(10f)
            )
                    .filter(evenPredicate)
        }
        "first" -> {
            ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            2f to 2,
                            4f to 3,
                            6f to 4,
                            8f to 5
                    ),
                    ObservableT.Termination.Complete(10f)
            )
                    .first()
        }
        "ignoreElements" -> {
            ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            2f to 2,
                            4f to 3,
                            6f to 4,
                            8f to 5
                    ),
                    ObservableT.Termination.Complete(10f)
            )
                    .ignoreElements()
        }
        "last" -> {
            ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            2f to 2,
                            4f to 3,
                            6f to 4,
                            8f to 5
                    ),
                    ObservableT.Termination.Complete(10f)
            )
                    .last()
        }
        "skip" -> {
            ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            2f to 2,
                            4f to 3,
                            6f to 4,
                            8f to 5
                    ),
                    termination = ObservableT.Termination.Complete(10f)
            )
                    .skip(2)
        }
        "skipLast" -> {
            ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            2f to 2,
                            4f to 3,
                            6f to 4,
                            8f to 5
                    ),
                    ObservableT.Termination.Complete(10f)
            )
                    .skipLast(2)
        }
        "take" -> {
            ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            2f to 2,
                            4f to 3,
                            6f to 4,
                            8f to 5
                    ),
                    ObservableT.Termination.Complete(10f)
            )
                    .take(2)
        }
        "takeLast" -> {
            ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            2f to 2,
                            4f to 3,
                            6f to 4,
                            8f to 5
                    ),
                    ObservableT.Termination.Complete(10f)
            )
                    .takeLast(2)
        }

        // Combine
        "combineLatest" -> {
            val observable1 = ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            4f to 2,
                            6f to 3
                    ),
                    ObservableT.Termination.Complete(8f)
            )
            val observable2 = ObservableX.inputOf(
                    listOf(
                            1f to 10,
                            3f to 20,
                            8f to 30
                    ),
                    ObservableT.Termination.Complete(10f)
            )

            ObservableX.combineLatest(
                    listOf(observable1, observable2),
                    functionOf<List<Int>, Int>("x, y -> x + y") { list -> list.sum() }
            )
        }
        "merge" -> {
            val observable1 = ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            4f to 2,
                            6f to 3
                    ),
                    ObservableT.Termination.Complete(8f)
            )
            val observable2 = ObservableX.inputOf(
                    listOf(
                            1f to 10,
                            3f to 20,
                            8f to 30
                    ),
                    ObservableT.Termination.Complete(10f)
            )

            ObservableX.merge(listOf(observable1, observable2))
        }
        "startWith" -> {
            ObservableX.inputOf(
                    listOf(
                            3f to 1,
                            5f to 2,
                            7f to 3
                    ),
                    ObservableT.Termination.Complete(10f)
            )
                    .startWith(0)
        }
        "zip" -> {
            val observable1 = ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            4f to 2,
                            6f to 3
                    ),
                    ObservableT.Termination.Complete(8f)
            )
            val observable2 = ObservableX.inputOf(
                    listOf(
                            1f to 10,
                            3f to 20,
                            8f to 30
                    ),
                    ObservableT.Termination.Complete(10f)
            )

            ObservableX.zip(
                    listOf(observable1, observable2),
                    functionOf<List<Int>, Int>("x, y -> x + y") { list -> list.sum() }
            )
        }

        // Utility
        "delay" -> {
            ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            2f to 2,
                            4f to 3
                    ),
                    ObservableT.Termination.Complete(6f)
            )
                    .delay(2f)
        }
        "timeout" -> {
            ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            2f to 2,
                            6f to 3
                    ),
                    ObservableT.Termination.Complete(8f)
            )
                    .timeout(3f)
        }

        // Conditional
        "amb" -> {
            val observable1 = ObservableX.inputOf(
                    listOf(
                            2f to 1,
                            5f to 2,
                            7f to 3
                    ),
                    ObservableT.Termination.Complete(10f)
            )
            val observable2 = ObservableX.inputOf(
                    listOf(
                            1f to 1,
                            3f to 2,
                            7f to 3
                    ),
                    ObservableT.Termination.Complete(8f)
            )

            ObservableX.amb(listOf(observable1, observable2))
        }
        "all" -> {
            ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            2f to 2,
                            4f to 3,
                            6f to 4,
                            8f to 5
                    ),
                    ObservableT.Termination.Complete(10f)
            )
                    .all(evenPredicate)
        }
        "any" -> {
            ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            2f to 2,
                            4f to 3,
                            6f to 4,
                            8f to 5
                    ),
                    ObservableT.Termination.Complete(10f)
            )
                    .any(evenPredicate)
        }
        // Custom
        "mapMerge" -> {
            val observable1 = ObservableX.inputOf(
                    listOf(
                            0f to 1,
                            4f to 2,
                            6f to 3
                    ),
                    ObservableT.Termination.Error(8f)
            ).map<Int, Int>(functionOf("x -> x * 2") { x -> x * 2 })

            val observable2 = ObservableX.inputOf(
                    listOf(
                            1f to 10,
                            3f to 20,
                            8f to 30
                    ),
                    ObservableT.Termination.Complete(10f)
            ).map<Int, Int>(functionOf("x -> x * 2") { x -> x * 2 })

            ObservableX.merge(listOf(observable1, observable2))
                    .map<Int, Int>(functionOf("x -> x / 2") { x -> x / 2 })
        }
        else -> null
    }
}
