package aodev.blue.rxsandbox.model.operator.single.transform

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.functions.Function
import aodev.blue.rxsandbox.model.operator.Operator


class SingleMap<T : Any, out R : Any>(private val mapping: Function<T, R>) : Operator {

    fun apply(input: SingleT<T>): SingleT<R> {
        return when (input.result) {
            is SingleT.Result.Success -> {
                SingleT(SingleT.Result.Success(input.result.time, mapping.apply(input.result.value)))
            }
            is SingleT.Result.Error -> SingleT(SingleT.Result.Error(input.result.time))
            is SingleT.Result.None -> SingleT(SingleT.Result.None())
        }
    }

    override val expression: String = "map { ${mapping.expression} }"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}map.html"
}
