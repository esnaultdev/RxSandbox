package aodev.blue.rxsandbox.model.completable


sealed class CompletableResult {
    object None : CompletableResult()
    class Complete(time: Float) : CompletableResult()
    class Error(time: Float) : CompletableResult()
}

data class CompletableTimeline(
        val result: CompletableResult
)
