package aodev.blue.rxsandbox.model.sample

import aodev.blue.rxsandbox.model.Operator


class OperatorSample<T, R>(
        val input: T,
        val operator: Operator<T, R>
)
