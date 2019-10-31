package aodev.blue.rxsandbox.model

import aodev.blue.rxsandbox.model.operator.Operator

/**
 * The base sealed class for the different async trees.
 * Note that the tree types are suffixed with X for "eXtended"
 * to avoid confusion and clashes with the ReactiveX types.
 */
sealed class ReactiveTypeX<out T : Any, out TL : Timeline<T>>(
        val innerX: InnerReactiveTypeX<T, TL>
)

sealed class InnerReactiveTypeX<out T : Any, out TL : Timeline<T>>(
        val previous: List<ReactiveTypeX<*, *>>
) {

    class Input<out T: Any, TL : Timeline<T>>(
            var input: TL
    ) : InnerReactiveTypeX<T, TL>(emptyList())

    class Result<out T: Any, TL : Timeline<T>>(
            val operator: Operator,
            previous: List<ReactiveTypeX<*, *>>,
            private val apply: () -> TL
    ) : InnerReactiveTypeX<T, TL>(previous) {

        private var cached: TL? = null

        fun invalidate() {
            cached = null
        }

        val isCached: Boolean
            get() = cached != null

        fun result(): TL = cached ?: apply().also { this.cached = it }
    }

    fun timeline(): TL {
        return when (this) {
            is Input -> input
            is Result -> result()
        }
    }
}

class ObservableX<out T : Any> : ReactiveTypeX<T, ObservableT<T>> {

    internal constructor(innerX: InnerReactiveTypeX<T, ObservableT<T>>) : super(innerX)

    companion object // Empty companion for extension functions
}

class SingleX<out T : Any> : ReactiveTypeX<T, SingleT<T>> {

    internal constructor(innerX: InnerReactiveTypeX<T, SingleT<T>>) : super(innerX)

    companion object // Empty companion for extension functions
}

class MaybeX<out T : Any> : ReactiveTypeX<T, MaybeT<T>> {

    internal constructor(innerX: InnerReactiveTypeX<T, MaybeT<T>>) : super(innerX)

    companion object // Empty companion for extension functions
}

class CompletableX : ReactiveTypeX<Nothing, CompletableT> {

    internal constructor(innerX: InnerReactiveTypeX<Nothing, CompletableT>) : super(innerX)

    companion object // Empty companion for extension functions
}
