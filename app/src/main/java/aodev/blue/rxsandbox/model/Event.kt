package aodev.blue.rxsandbox.model


data class Event<out T>(
        val time: Float,
        val value: T
)

typealias TerminationEvent = Event<Termination>