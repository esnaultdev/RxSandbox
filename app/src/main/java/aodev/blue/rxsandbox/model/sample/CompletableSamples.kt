package aodev.blue.rxsandbox.model.sample

import aodev.blue.rxsandbox.model.AsyncTree
import aodev.blue.rxsandbox.model.CompletableT
import aodev.blue.rxsandbox.model.CompletableX
import aodev.blue.rxsandbox.model.operator.completable.*


fun getCompletableSample(operatorName: String): AsyncTree<Int>? {
    return when (operatorName) {
        // Utility
        "delay" -> {
            CompletableX.inputOf(CompletableT.Result.Complete(3f))
                    .delay(2f)
        }
        else -> null
    }
}