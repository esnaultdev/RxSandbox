package aodev.blue.rxsandbox.model.sample

import aodev.blue.rxsandbox.model.MaybeT
import aodev.blue.rxsandbox.model.operator.maybe.utility.MaybeDelay


fun getMaybeSample(operatorName: String): OperatorSample? {
    return when (operatorName) {
        // Utility
        "delay" -> {
            OperatorSample(
                    input = listOf(
                            MaybeT(MaybeT.Result.Success(3f, 0))
                    ),
                    operator = MaybeDelay(2f)
            )
        }
        else -> null
    }
}