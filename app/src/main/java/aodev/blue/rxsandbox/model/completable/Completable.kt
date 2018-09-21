package aodev.blue.rxsandbox.model.completable


sealed class CompletableResult {
    object None : CompletableResult()
    class Complete(val time: Float) : CompletableResult()
    class Error(val time: Float) : CompletableResult()
}

data class CompletableTimeline(
        val result: CompletableResult
)
