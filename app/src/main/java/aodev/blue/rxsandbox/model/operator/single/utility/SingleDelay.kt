package aodev.blue.rxsandbox.model.operator.single.utility

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Input
import aodev.blue.rxsandbox.model.operator.Operator


class SingleDelay<T>(private val delay: Float) : Operator<T, T> {

    init {
        require(delay >= 0)
    }

    override fun apply(input: List<Timeline<T>>): Timeline<T>? {
        return Input.Single.from(input) {
            apply(it)
        }
    }

    fun apply(input: SingleT<T>): SingleT<T> {
        val result = input.result
        val delayedResult: SingleT.Result<T> = when (result) {
            is SingleT.Result.None -> SingleT.Result.None()
            is SingleT.Result.Error -> {
                val newTime = result.time + delay
                if (newTime <= Config.timelineDuration) {
                    SingleT.Result.Error(newTime)
                } else {
                    SingleT.Result.None()
                }
            }
            is SingleT.Result.Success -> {
                val newTime = result.time + delay
                if (newTime <= Config.timelineDuration) {
                    SingleT.Result.Success(newTime, result.value)
                } else {
                    SingleT.Result.None()
                }
            }
        }

        return SingleT(delayedResult)
    }

    override fun expression(): String {
        return "delay(${"%.1f".format(delay)})"
    }
}