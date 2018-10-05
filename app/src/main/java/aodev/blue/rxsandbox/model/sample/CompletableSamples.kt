package aodev.blue.rxsandbox.model.sample

import aodev.blue.rxsandbox.model.CompletableT
import aodev.blue.rxsandbox.model.operator.completable.utility.CompletableDelay


fun getCompletableSample(operatorName: String): OperatorSample? {
    return when (operatorName) {
        // Utility
        "delay" -> {
            OperatorSample(
                    input = listOf(
                            CompletableT(CompletableT.Result.Complete(3f))
                    ),
                    operator = CompletableDelay(2f)
            )
        }
        else -> null
    }
}