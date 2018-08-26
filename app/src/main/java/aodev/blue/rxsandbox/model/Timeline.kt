package aodev.blue.rxsandbox.model


data class Timeline<out T>(
    val events: List<Event<T>>,
    val termination: TerminationEvent?
)