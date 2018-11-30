package aodev.blue.rxsandbox.model.operator.completable.utility

import aodev.blue.rxsandbox.model.CompletableT
import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.operator.Operator


class CompletableDelay(private val delay: Float) : Operator {

    init {
        require(delay >= 0)
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

    override val expression = "delay(${"%.1f".format(delay)})"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}delay.html"
}
