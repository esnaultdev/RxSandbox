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
        val events: Set<ObservableEvent<T>>,
        val termination: ObservableTermination
) {

    val sortedEvents: List<ObservableEvent<T>> by lazy(LazyThreadSafetyMode.NONE) {
        events.sortedBy { it.time }
    }
}
