package aodev.blue.rxsandbox.model.sample

import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.operator.single.utility.SingleDelay


fun getSingleSample(operatorName: String): OperatorSample? {
    return when (operatorName) {
        // Utility
        "delay" -> {
            OperatorSample(
                    input = listOf(
                            SingleT(SingleT.Result.Success(3f, 0))
                    ),
                    operator = SingleDelay(2f)
            )
        }
        else -> null
    }
}