package aodev.blue.rxsandbox.model.sample

import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator


class OperatorSample(
        val input: List<Timeline<Int>>,
        val operator: Operator<Int, Int>
)
