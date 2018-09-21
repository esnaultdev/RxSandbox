package aodev.blue.rxsandbox.model.completable.operators.utility

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.Operator
import aodev.blue.rxsandbox.model.completable.CompletableResult
import aodev.blue.rxsandbox.model.completable.CompletableTimeline


class CompletableDelay(
        private val delay: Float
) : Operator<CompletableTimeline, CompletableTimeline> {

    init {
        require(delay >= 0)
    }

    override fun apply(input: CompletableTimeline): CompletableTimeline {
        val delayedResult = when (input.result) {
            CompletableResult.None -> CompletableResult.None
            is CompletableResult.Error -> {
                val newTime = input.result.time + delay
                if (newTime <= Config.timelineDuration) {
                    CompletableResult.Error(newTime)
                } else {
                    CompletableResult.None
                }
            }
            is CompletableResult.Complete -> {
                val newTime = input.result.time + delay
                if (newTime <= Config.timelineDuration) {
                    CompletableResult.Complete(newTime)
                } else {
                    CompletableResult.None
                }
            }
        }

        return CompletableTimeline(delayedResult)
    }

    override fun expression(): String {
        return "delay(${"%.1f".format(delay)})"
    }
}