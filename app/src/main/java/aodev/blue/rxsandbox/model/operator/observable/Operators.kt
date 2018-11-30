@file:Suppress("unused")

package aodev.blue.rxsandbox.model.operator.observable

import aodev.blue.rxsandbox.model.CompletableX
import aodev.blue.rxsandbox.model.ObservableX
import aodev.blue.rxsandbox.model.SingleX
import aodev.blue.rxsandbox.model.functions.Function
import aodev.blue.rxsandbox.model.functions.Function2
import aodev.blue.rxsandbox.model.functions.Predicate
import aodev.blue.rxsandbox.model.operator.observable.combine.ObservableCombineLatest
import aodev.blue.rxsandbox.model.operator.observable.combine.ObservableMerge
import aodev.blue.rxsandbox.model.operator.observable.combine.ObservableStartWith
import aodev.blue.rxsandbox.model.operator.observable.conditional.ObservableAll
import aodev.blue.rxsandbox.model.operator.observable.conditional.ObservableAmb
import aodev.blue.rxsandbox.model.operator.observable.conditional.ObservableAny
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
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableElementAt
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableFilter
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableFirst
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableIgnoreElements
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableLast
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableSkip
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableSkipLast
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableTake
import aodev.blue.rxsandbox.model.operator.observable.filter.ObservableTakeLast
import aodev.blue.rxsandbox.model.operator.observable.transform.ObservableMap
import aodev.blue.rxsandbox.model.operator.observable.transform.ObservableScan
import aodev.blue.rxsandbox.model.operator.observable.utility.ObservableDelay
import aodev.blue.rxsandbox.model.operator.observable.utility.ObservableTimeout


// region Combine

fun <T : Any, R : Any> ObservableX.Companion.combineLatest(
        input: List<ObservableX<T>>,
        combiner: Function<List<T>, R>
): ObservableX<R> {
    val operator = ObservableCombineLatest(combiner)
    return ObservableX.Result(operator, input) {
        operator.apply(input.map { it.observableT() })
    }
}

fun <T : Any> ObservableX.Companion.merge(input: List<ObservableX<T>>): ObservableX<T> {
    val operator = ObservableMerge<T>()
    return ObservableX.Result(operator, input) {
        operator.apply(input.map { it.observableT() })
    }
}

fun <T : Any> ObservableX<T>.startWith(value: T): ObservableX<T> {
    val operator = ObservableStartWith(value)
    return ObservableX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

// endregion

// region Conditional

fun <T : Any> ObservableX<T>.all(predicate: Predicate<T>): SingleX<Boolean> {
    val operator = ObservableAll(predicate)
    return SingleX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

fun <T : Any> ObservableX.Companion.amb(input: List<ObservableX<T>>): ObservableX<T> {
    val operator = ObservableAmb<T>()
    return ObservableX.Result(operator, input) {
        operator.apply(input.map { it.observableT() })
    }
}

fun <T : Any> ObservableX<T>.any(predicate: Predicate<T>): SingleX<Boolean> {
    val operator = ObservableAny(predicate)
    return SingleX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

// endregion

// region Create

fun <T : Any> ObservableX.Companion.empty(): ObservableX<T> {
    val operator = ObservableEmpty<T>()
    return ObservableX.Result(operator, emptyList(), operator::apply)
}

fun ObservableX.Companion.interval(interval: Float): ObservableX<Int> {
    val operator = ObservableInterval(interval)
    return ObservableX.Result(operator, emptyList(), operator::apply)
}

fun <T: Any> ObservableX.Companion.just(vararg values: T): ObservableX<T> {
    val operator = ObservableJust(*values)
    return ObservableX.Result(operator, emptyList(), operator::apply)
}

fun <T: Any> ObservableX.Companion.never(): ObservableX<T> {
    val operator = ObservableNever<T>()
    return ObservableX.Result(operator, emptyList(), operator::apply)
}

fun ObservableX.Companion.range(from: Int, to: Int): ObservableX<Int> {
    val operator = ObservableRange(from, to)
    return ObservableX.Result(operator, emptyList(), operator::apply)
}

fun <T: Any> ObservableX<T>.repeat(): ObservableX<T> {
    val operator = ObservableRepeat<T>()
    return ObservableX.Result(operator, emptyList()) {
        operator.apply(observableT())
    }
}

fun <T: Any> ObservableX.Companion.`throw`(): ObservableX<T> {
    val operator = ObservableThrow<T>()
    return ObservableX.Result(operator, emptyList(), operator::apply)
}

fun ObservableX.Companion.timer(delay: Float): ObservableX<Int> {
    val operator = ObservableTimer(delay)
    return ObservableX.Result(operator, emptyList(), operator::apply)
}

// endregion

// region Filter

fun <T : Any> ObservableX<T>.debounce(duration: Float): ObservableX<T> {
    val operator = ObservableDebounce<T>(duration)
    return ObservableX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

fun <T : Any> ObservableX<T>.distinct(): ObservableX<T> {
    val operator = ObservableDistinct<T>()
    return ObservableX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

fun <T : Any> ObservableX<T>.distinctUntilChanged(): ObservableX<T> {
    val operator = ObservableDistinctUntilChanged<T>()
    return ObservableX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

fun <T : Any> ObservableX<T>.elementAt(index: Int): SingleX<T> {
    val operator = ObservableElementAt<T>(index)
    return SingleX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

fun <T : Any> ObservableX<T>.filter(predicate: Predicate<T>): ObservableX<T> {
    val operator = ObservableFilter(predicate)
    return ObservableX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

fun <T : Any> ObservableX<T>.first(): SingleX<T> {
    val operator = ObservableFirst<T>()
    return SingleX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

fun <T : Any> ObservableX<T>.ignoreElements(): CompletableX {
    val operator = ObservableIgnoreElements<T>()
    return CompletableX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

fun <T : Any> ObservableX<T>.last(): SingleX<T> {
    val operator = ObservableLast<T>()
    return SingleX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

fun <T : Any> ObservableX<T>.skip(count: Int): ObservableX<T> {
    val operator = ObservableSkip<T>(count)
    return ObservableX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

fun <T : Any> ObservableX<T>.skipLast(count: Int): ObservableX<T> {
    val operator = ObservableSkipLast<T>(count)
    return ObservableX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

fun <T : Any> ObservableX<T>.take(count: Int): ObservableX<T> {
    val operator = ObservableTake<T>(count)
    return ObservableX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

fun <T : Any> ObservableX<T>.takeLast(count: Int): ObservableX<T> {
    val operator = ObservableTakeLast<T>(count)
    return ObservableX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

// endregion

// region Transform

fun <T : Any, R : Any> ObservableX<T>.map(combiner: Function<T, R>): ObservableX<R> {
    val operator = ObservableMap(combiner)
    return ObservableX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

fun <T : Any, R : Any> ObservableX<T>.scan(
        initialValue: R,
        operation: Function2<R, T, R>
): ObservableX<R> {
    val operator = ObservableScan(initialValue, operation)
    return ObservableX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

// endregion

// region Delay

fun <T : Any> ObservableX<T>.delay(delay: Float): ObservableX<T> {
    val operator = ObservableDelay<T>(delay)
    return ObservableX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

fun <T : Any> ObservableX<T>.timeout(timeout: Float): ObservableX<T> {
    val operator = ObservableTimeout<T>(timeout)
    return ObservableX.Result(operator, listOf(this)) {
        operator.apply(observableT())
    }
}

// endregion
