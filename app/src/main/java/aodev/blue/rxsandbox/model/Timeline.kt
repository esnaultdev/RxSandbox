package aodev.blue.rxsandbox.model


/**
 * The base sealed class for the different timeline types.
 * Note that the timeline types are suffixed with T for Timeline
 * to avoid confusion and clashes with the ReactiveX types.
 */
sealed class Timeline<out T : Any>


data class ObservableT<out T : Any>(
        val events: List<Event<T>>,
        val termination: Termination
) : Timeline<T>() {

    companion object {
        fun <T> eventsOf(vararg events: Pair<Float, T>): List<Event<T>> {
            return events.map { Event(it.first, it.second) }
        }
    }

    init {
        val eventsSorted = events.zip(events.drop(1)).all { (event, nextEvent) ->
            event.time <= nextEvent.time
        }
        require(eventsSorted) { "Observable events not sorted" }
    }

    data class Event<out T>(
            val time: Float,
            val value: T
    ) {
        fun moveTo(time: Float) = copy(time = time)
    }

    sealed class Termination {
        object None : Termination()
        class Complete(val time: Float) : Termination()
        class Error(val time: Float) : Termination()
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

    sealed class Result<out T> {
        class None<T> : Result<T>()
        class Success<T>(val time: Float, val value: T) : Result<T>()
        class Error<T>(val time: Float) : Result<T>()
    }
}


data class MaybeT<out T : Any>(
        val result: Result<T>
) : Timeline<T>() {

    sealed class Result<out T> {
        class None<T> : Result<T>()
        class Complete<T>(val time: Float) : Result<T>()
        class Success<T>(val time: Float, val value: T) : Result<T>()
        class Error<T>(val time: Float) : Result<T>()
    }
}


data class CompletableT(
        val result: Result
) : Timeline<Nothing>() {

    sealed class Result {
        object None : Result()
        class Complete(val time: Float) : Result()
        class Error(val time: Float) : Result()
    }
}
