package aodev.blue.rxsandbox.model.observable


data class ObservableEvent<out T>(
        val time: Float,
        val value: T
) {
    fun moveTo(time: Float) = copy(time = time)
}

sealed class ObservableTermination {
    object None : ObservableTermination()
    class Complete(val time: Float) : ObservableTermination()
    class Error(val time: Float) : ObservableTermination()
}

data class ObservableTimeline<out T>(
        val events: List<ObservableEvent<T>>,
        val termination: ObservableTermination
) {
    init {
        val eventsSorted = events.zip(events.drop(1)).all { (event, nextEvent) ->
            event.time <= nextEvent.time
        }
        require(eventsSorted) { "Observable events not sorted" }
    }
}
