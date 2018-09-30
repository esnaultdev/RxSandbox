package aodev.blue.rxsandbox.model.operator.completable.utility

import aodev.blue.rxsandbox.model.CompletableT
import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Input
import aodev.blue.rxsandbox.model.operator.Operator


class CompletableDelay<T>(private val delay: Float) : Operator<T, T> {

    init {
        require(delay >= 0)
    }

    override fun apply(input: List<Timeline<T>>): Timeline<T>? {
        return Input.Completable.from(input) {
            apply(it)
        }
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
