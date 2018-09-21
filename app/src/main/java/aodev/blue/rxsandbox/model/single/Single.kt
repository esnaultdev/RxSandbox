package aodev.blue.rxsandbox.model.single


sealed class SingleResult<out T> {
    class None<T> : SingleResult<T>()
    class Success<T>(val time: Float, val value: T) : SingleResult<T>()
    class Error<T>(val time: Float) : SingleResult<T>()
}

data class SingleTimeline<out T>(
        val result: SingleResult<T>
)
