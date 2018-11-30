package aodev.blue.rxsandbox.model.sample

import aodev.blue.rxsandbox.model.AsyncTree
import aodev.blue.rxsandbox.model.MaybeT
import aodev.blue.rxsandbox.model.MaybeX
import aodev.blue.rxsandbox.model.operator.maybe.*


fun getMaybeSample(operatorName: String): AsyncTree<Int>? {
    return when (operatorName) {
        // Utility
        "delay" -> {
            MaybeX.inputOf(MaybeT.Result.Success(3f, 0))
                    .delay(2f)
        }
        else -> null
    }
}