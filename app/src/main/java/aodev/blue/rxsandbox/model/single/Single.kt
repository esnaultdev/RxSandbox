package aodev.blue.rxsandbox.model.single


sealed class SingleResult<out T> {
    class None<T> : SingleResult<T>()
    class Success<T>(time: Float, value: T) : SingleResult<T>()
    class Error<T>(time: Float) : SingleResult<T>()
}

data class SingleTimeline<out T>(
        val result: SingleResult<T>
)
