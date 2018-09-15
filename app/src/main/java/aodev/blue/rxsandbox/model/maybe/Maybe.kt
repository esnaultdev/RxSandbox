package aodev.blue.rxsandbox.model.maybe


sealed class MaybeResult<out T> {
    class None<T> : MaybeResult<T>()
    class Complete<T>(time: Float) : MaybeResult<T>()
    class Success<T>(time: Float, value: T) : MaybeResult<T>()
    class Error<T>(time: Float) : MaybeResult<T>()
}

data class MaybeTimeline<out T>(
        val result: MaybeResult<T>
)
