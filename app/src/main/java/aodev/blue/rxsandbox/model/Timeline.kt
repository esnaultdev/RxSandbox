package aodev.blue.rxsandbox.model


data class Timeline<out T>(
    val events: Set<Event<T>>,
    val termination: TerminationEvent?
) {

    val sortedEvents: List<Event<T>> by lazy(LazyThreadSafetyMode.NONE) {
        events.sortedBy { it.time }
    }
}