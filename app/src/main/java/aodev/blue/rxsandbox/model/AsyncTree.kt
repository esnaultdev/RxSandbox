package aodev.blue.rxsandbox.model

import aodev.blue.rxsandbox.model.operator.Operator

/**
 * The base sealed class for the different async trees.
 * Note that the tree types are suffixed with X for "eXtended"
 * to avoid confusion and clashes with the ReactiveX types.
 */
sealed class AsyncTree<out T : Any>

sealed class ObservableX<out T : Any> : AsyncTree<T>() {
    class Input<T: Any>(var input: ObservableT<T>) : ObservableX<T>()
    class Result<T: Any>(
            val operator: Operator,
            val previous: List<AsyncTree<*>>,
            val apply: () -> ObservableT<T>
    ) : ObservableX<T>()

    operator fun invoke(): ObservableT<T> {
        return when (this) {
            is Input -> input
            is Result -> apply()
        }
    }

    companion object // Empty companion for extension functions
}

sealed class SingleX<out T : Any> : AsyncTree<T>() {
    class Input<T: Any>(var input: SingleT<T>) : SingleX<T>()
    class Result<T: Any>(
            val operator: Operator,
            val previous: List<AsyncTree<*>>,
            val apply: () -> SingleT<T>
    ) : SingleX<T>()

    operator fun invoke(): SingleT<T> {
        return when (this) {
            is Input -> input
            is Result -> apply()
        }
    }

    companion object // Empty companion for extension functions
}

sealed class MaybeX<out T : Any> : AsyncTree<T>() {
    class Input<T: Any>(var input: MaybeT<T>) : MaybeX<T>()
    class Result<T: Any>(
            val operator: Operator,
            val previous: List<AsyncTree<*>>,
            val apply: () -> MaybeT<T>
    ) : MaybeX<T>()

    operator fun invoke(): MaybeT<T> {
        return when (this) {
            is Input -> input
            is Result -> apply()
        }
    }

    companion object // Empty companion for extension functions
}

sealed class CompletableX : AsyncTree<Nothing>() {
    class Input(var input: CompletableT) : CompletableX()
    class Result(
            val operator: Operator,
            val previous: List<AsyncTree<*>>,
            val apply: () -> CompletableT
    ) : CompletableX()

    operator fun invoke(): CompletableT {
        return when (this) {
            is Input -> input
            is Result -> apply()
        }
    }

    companion object // Empty companion for extension functions
}
