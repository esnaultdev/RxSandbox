package aodev.blue.rxsandbox.model.maybe


sealed class MaybeResult<out T> {
    class None<T> : MaybeResult<T>()
    class Complete<T>(val time: Float) : MaybeResult<T>()
    class Success<T>(val time: Float, val value: T) : MaybeResult<T>()
    class Error<T>(val time: Float) : MaybeResult<T>()
}

data class MaybeTimeline<out T>(
        val result: MaybeResult<T>
)
