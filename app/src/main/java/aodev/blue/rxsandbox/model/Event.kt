package aodev.blue.rxsandbox.model


data class Event<out T>(
        val time: Float,
        val value: T
) {
    fun moveTo(time: Float) = copy(time = time)
}

typealias TerminationEvent = Event<Termination>