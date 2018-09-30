package aodev.blue.rxsandbox.model.operator.completable.utility

import aodev.blue.rxsandbox.model.CompletableT
import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.model.operator.ParamsCompletable


class CompletableDelay(
        private val delay: Float
) : Operator<Unit, Unit, ParamsCompletable, CompletableT> {

    init {
        require(delay >= 0)
    }

    override fun params(input: List<Timeline<Unit>>): ParamsCompletable? {
        return ParamsCompletable.fromInput(input)
    }

    override fun apply(params: ParamsCompletable): CompletableT {
        return apply(params.completable)
    }

    fun apply(input: CompletableT): CompletableT {
        val delayedResult = when (input.result) {
            CompletableT.Result.None -> CompletableT.Result.None
            is CompletableT.Result.Error -> {
                val newTime = input.result.time + delay
                if (newTime <= Config.timelineDuration) {
                    CompletableT.Result.Error(newTime)
                } else {
                    CompletableT.Result.None
                }
            }
            is CompletableT.Result.Complete -> {
                val newTime = input.result.time + delay
                if (newTime <= Config.timelineDuration) {
                    CompletableT.Result.Complete(newTime)
                } else {
                    CompletableT.Result.None
                }
            }
        }

        return CompletableT(delayedResult)
    }

    override fun expression(): String {
        return "delay(${"%.1f".format(delay)})"
    }
}
