package aodev.blue.rxsandbox.model.operator.single.create

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.operator.Operator


class SingleJust<T : Any>(private val value: T) : Operator {

    fun apply(): SingleT<T> {
        return SingleT(SingleT.Result.Success(0f, value))
    }

    override val expression: String = "just($value)"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}just.html"
}
