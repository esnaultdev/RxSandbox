package aodev.blue.rxsandbox.model


fun <T : Any> ObservableT.Companion.inputOf(
        events: List<Pair<Float, T>>,
        termination: ObservableT.Termination
): ObservableT<T> {
    return ObservableT(events.map { ObservableT.Event(it.first, it.second) }, termination)
}
