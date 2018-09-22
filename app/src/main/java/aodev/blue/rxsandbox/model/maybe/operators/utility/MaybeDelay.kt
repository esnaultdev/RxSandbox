package aodev.blue.rxsandbox.model.maybe.operators.utility

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.maybe.MaybeResult
import aodev.blue.rxsandbox.model.maybe.MaybeTimeline


class MaybeDelay<T>(
        private val delay: Float
) : Operator<MaybeTimeline<T>, MaybeTimeline<T>> {

    init {
        require(delay >= 0)
    }

    override fun apply(input: MaybeTimeline<T>): MaybeTimeline<T> {
        val result = input.result
        val delayedResult: MaybeResult<T> = when (result) {
            is MaybeResult.None -> MaybeResult.None()
            is MaybeResult.Error -> {
                val newTime = result.time + delay
                if (newTime <= Config.timelineDuration) {
                    MaybeResult.Error(newTime)
                } else {
                    MaybeResult.None()
                }
            }
            is MaybeResult.Complete -> {
                val newTime = result.time + delay
                if (newTime <= Config.timelineDuration) {
                    MaybeResult.Complete(newTime)
                } else {
                    MaybeResult.None()
                }
            }
            is MaybeResult.Success -> {
                val newTime = result.time + delay
                if (newTime <= Config.timelineDuration) {
                    MaybeResult.Success(newTime, result.value)
                } else {
                    MaybeResult.None()
                }
            }
        }

        return MaybeTimeline(delayedResult)
    }

    override fun expression(): String {
        return "delay(${"%.1f".format(delay)})"
    }
}