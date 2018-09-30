package aodev.blue.rxsandbox.model.operator.maybe.utility

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.MaybeT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.ParamsMaybe


class MaybeDelay<T>(
        private val delay: Float
) : Operator<T, T, ParamsMaybe<T>, MaybeT<T>> {

    init {
        require(delay >= 0)
    }

    override fun params(input: List<Timeline<T>>): ParamsMaybe<T>? {
        return ParamsMaybe.fromInput(input)
    }

    override fun apply(params: ParamsMaybe<T>): MaybeT<T> {
        return apply(params.maybe)
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

    override fun expression(): String {
        return "delay(${"%.1f".format(delay)})"
    }
}