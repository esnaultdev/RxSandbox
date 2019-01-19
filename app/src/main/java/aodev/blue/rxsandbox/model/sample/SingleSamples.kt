package aodev.blue.rxsandbox.model.sample

import aodev.blue.rxsandbox.model.ReactiveTypeX
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.SingleX
import aodev.blue.rxsandbox.model.functions.functionOf
import aodev.blue.rxsandbox.model.operator.single.*


fun getSingleSample(operatorName: String): ReactiveTypeX<*, *>? {
    return when (operatorName) {
        // Create
        "just" -> SingleX.just(5)

        // Transform
        "map" -> {
            SingleX.inputOf(SingleT.Result.Success(5f, 4))
                    .map<Int, Int>(functionOf("x -> x * 2") { x -> x * 2 })
        }

        // Utility
        "delay" -> {
            SingleX.inputOf(SingleT.Result.Success(3f, 0))
                    .delay(2f)
        }
        else -> null
    }
}