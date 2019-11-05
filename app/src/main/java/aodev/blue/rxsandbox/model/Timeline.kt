package aodev.blue.rxsandbox.model


/**
 * The base sealed class for the different timeline types.
 * Note that the timeline types are suffixed with T for Timeline
 * to avoid confusion and clashes with the ReactiveX types.
 */
sealed class Timeline<out T : Any> {
    abstract val type: TimelineType
}


data class ObservableT<out T : Any>(
        val events: List<Event<T>>,
        val termination: Termination
) : Timeline<T>() {

    companion object; // Empty companion for extension functions

    override val type = TimelineType.OBSERVABLE

    init {
        val eventsSorted = events.asSequence()
                .zipWithNext()
                .all { (event, nextEvent) -> event.time <= nextEvent.time }
        require(eventsSorted) { "Observable events not sorted" }
    }

    data class Event<out T>(
            val time: Float,
            val value: T
    ) {
        fun moveTo(time: Float) = copy(time = time)
    }

    sealed class Termination {
        abstract val time: Float?

        object None : Termination() {
            override val time: Float? = null
        }
        data class Complete(override val time: Float) : Termination()
        data class Error(override val time: Float) : Termination()
    }
}

val ObservableT.Termination.time: Float?
    get() = when (this) {
        is ObservableT.Termination.None -> null
        is ObservableT.Termination.Complete -> this.time
        is ObservableT.Termination.Error -> this.time
    }


data class SingleT<out T : Any>(
        val result: Result<T>
) : Timeline<T>() {

    override val type = TimelineType.SINGLE

    sealed class Result<out T> {
        abstract val time: Float?

        class None<T> : Result<T>() {
            override val time: Float? = null
        }
        data class Success<T>(override val time: Float, val value: T) : Result<T>()
        data class Error<T>(override val time: Float) : Result<T>()
    }
}


data class MaybeT<out T : Any>(
        val result: Result<T>
) : Timeline<T>() {

    override val type = TimelineType.MAYBE

    sealed class Result<out T> {
        abstract val time: Float?

        class None<T> : Result<T>() {
            override val time: Float? = null
        }
        data class Complete<T>(override val time: Float) : Result<T>()
        data class Success<T>(override val time: Float, val value: T) : Result<T>()
        data class Error<T>(override val time: Float) : Result<T>()
    }
}


data class CompletableT(
        val result: Result
) : Timeline<Nothing>() {

    override val type = TimelineType.COMPLETABLE

    sealed class Result {
        abstract val time: Float?

        object None : Result() {
            override val time: Float? = null
        }
        data class Complete(override val time: Float) : Result()
        data class Error(override val time: Float) : Result()
    }
}
