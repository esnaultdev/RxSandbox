package aodev.blue.rxsandbox.model.single.operators.utility

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.single.SingleResult
import aodev.blue.rxsandbox.model.single.SingleTimeline


class SingleDelay<T>(
        private val delay: Float
) : Operator<SingleTimeline<T>, SingleTimeline<T>> {

    init {
        require(delay >= 0)
    }

    override fun apply(input: SingleTimeline<T>): SingleTimeline<T> {
        val result = input.result
        val delayedResult: SingleResult<T> = when (result) {
            is SingleResult.None -> SingleResult.None()
            is SingleResult.Error -> {
                val newTime = result.time + delay
                if (newTime <= Config.timelineDuration) {
                    SingleResult.Error(newTime)
                } else {
                    SingleResult.None()
                }
            }
            is SingleResult.Success -> {
                val newTime = result.time + delay
                if (newTime <= Config.timelineDuration) {
                    SingleResult.Success(newTime, result.value)
                } else {
                    SingleResult.None()
                }
            }
        }

        return SingleTimeline(delayedResult)
    }

    override fun expression(): String {
        return "delay(${"%.1f".format(delay)})"
    }
}