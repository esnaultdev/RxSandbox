package aodev.blue.rxsandbox.model


fun <T : Any> ObservableT.Companion.inputOf(
        events: List<Pair<Float, T>>,
        termination: ObservableT.Termination
): ObservableT<T> {
    return ObservableT(events.map { ObservableT.Event(it.first, it.second) }, termination)
}

fun <T : Any> ObservableT.Companion.never() =
        ObservableT<T>(events = emptyList(), termination = ObservableT.Termination.None)

fun <T : Any> ObservableT.Companion.empty(completeAt: Float? = null): ObservableT<T> {
    val termination = ObservableT.Termination.Complete(completeAt ?: 0f)
    return ObservableT(events = emptyList(), termination = termination)
}