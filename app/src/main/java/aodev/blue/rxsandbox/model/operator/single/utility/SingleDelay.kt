package aodev.blue.rxsandbox.model.operator.single.utility

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.ParamsSingle


class SingleDelay<T>(
        private val delay: Float
) : Operator<T, T, ParamsSingle<T>, SingleT<T>> {

    init {
        require(delay >= 0)
    }

    override fun params(input: List<Timeline<T>>): ParamsSingle<T>? {
        return ParamsSingle.fromInput(input)
    }

    override fun apply(params: ParamsSingle<T>): SingleT<T> {
        return apply(params.single)
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