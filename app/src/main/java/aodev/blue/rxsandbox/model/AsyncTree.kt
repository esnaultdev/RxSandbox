package aodev.blue.rxsandbox.model

import aodev.blue.rxsandbox.model.operator.Operator

/**
 * The base sealed class for the different async trees.
 * Note that the tree types are suffixed with X for "eXtended"
 * to avoid confusion and clashes with the ReactiveX types.
 */
sealed class AsyncTree<out T : Any>(val previous: List<AsyncTree<*>>) {

    fun timeline(): Timeline<T> {
        return when (this) {
            is ObservableX -> this.observableT()
            is SingleX -> this.singleT()
            is MaybeX -> this.maybeT()
            is CompletableX -> this.completableT()
        }
    }
}

sealed class ObservableX<out T : Any>(
        previous: List<AsyncTree<*>>
) : AsyncTree<T>(previous) {

    class Input<T: Any>(var input: ObservableT<T>) : ObservableX<T>(emptyList())
    class Result<T: Any>(
            val operator: Operator,
            previous: List<AsyncTree<*>>,
            val apply: () -> ObservableT<T>
    ) : ObservableX<T>(previous)

    fun observableT(): ObservableT<T> {
        return when (this) {
            is Input -> input
            is Result -> apply()
        }
    }

    companion object // Empty companion for extension functions
}

sealed class SingleX<out T : Any>(
        previous: List<AsyncTree<*>>
) : AsyncTree<T>(previous) {

    class Input<T: Any>(var input: SingleT<T>) : SingleX<T>(emptyList())
    class Result<T: Any>(
            val operator: Operator,
            previous: List<AsyncTree<*>>,
            val apply: () -> SingleT<T>
    ) : SingleX<T>(previous)

    fun singleT(): SingleT<T> {
        return when (this) {
            is Input -> input
            is Result -> apply()
        }
    }

    companion object // Empty companion for extension functions
}

sealed class MaybeX<out T : Any>(
        previous: List<AsyncTree<*>>
) : AsyncTree<T>(previous) {

    class Input<T: Any>(var input: MaybeT<T>) : MaybeX<T>(emptyList())
    class Result<T: Any>(
            val operator: Operator,
            previous: List<AsyncTree<*>>,
            val apply: () -> MaybeT<T>
    ) : MaybeX<T>(previous)

    fun maybeT(): MaybeT<T> {
        return when (this) {
            is Input -> input
            is Result -> apply()
        }
    }

    companion object // Empty companion for extension functions
}

sealed class CompletableX(
        previous: List<AsyncTree<*>>
) : AsyncTree<Nothing>(previous) {

    class Input(var input: CompletableT) : CompletableX(emptyList())
    class Result(
            val operator: Operator,
            previous: List<AsyncTree<*>>,
            val apply: () -> CompletableT
    ) : CompletableX(previous)

    fun completableT(): CompletableT {
        return when (this) {
            is Input -> input
            is Result -> apply()
        }
    }

    companion object // Empty companion for extension functions
}
