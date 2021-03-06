package aodev.blue.rxsandbox.model.operator.maybe.utility

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.MaybeT
import aodev.blue.rxsandbox.model.operator.Operator


class MaybeDelay<T : Any>(private val delay: Float) : Operator {

    init {
        require(delay >= 0)
    }

    fun apply(input: MaybeT<T>): MaybeT<T> {
        val result = input.result
        val delayedResult: MaybeT.Result<T> = when (result) {
            is MaybeT.Result.None -> MaybeT.Result.None()
            is MaybeT.Result.Error -> {
                val newTime = result.time + delay
                if (newTime <= Config.timelineDuration) {
                    MaybeT.Result.Error(newTime)
                } else {
                    MaybeT.Result.None()
                }
            }
            is MaybeT.Result.Complete -> {
                val newTime = result.time + delay
                if (newTime <= Config.timelineDuration) {
                    MaybeT.Result.Complete(newTime)
                } else {
                    MaybeT.Result.None()
                }
            }
            is MaybeT.Result.Success -> {
                val newTime = result.time + delay
                if (newTime <= Config.timelineDuration) {
                    MaybeT.Result.Success(newTime, result.value)
                } else {
                    MaybeT.Result.None()
                }
            }
        }

        return MaybeT(delayedResult)
    }

    override val expression: String = "delay(${"%.1f".format(delay)})"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}delay.html"
}