package aodev.blue.rxsandbox.model.operator.observable.create

import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Input
import aodev.blue.rxsandbox.model.operator.Operator


class ObservableRange<T>(private val from: Int, private val to: Int) : Operator<T, Int> {

    init {
        require(from >= 0)
        require(to >= 0)
    }

    override fun apply(input: List<Timeline<T>>): Timeline<Int>? {
        return Input.None.from(input) {
            apply()
        }
    }

    fun apply(): ObservableT<Int> {
        return ObservableT(
                (from..to).map { ObservableT.Event(0f, it) },
                ObservableT.Termination.Complete(0f)
        )
    }

    override val expression: String = "range($from, $to)"

    override val docUrl: String? = "${Config.operatorDocUrlPrefix}range.html"
}