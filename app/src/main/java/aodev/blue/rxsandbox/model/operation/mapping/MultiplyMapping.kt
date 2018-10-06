package aodev.blue.rxsandbox.model.operation.mapping


class MultiplyMapping(private val factor: Int) : Mapping<Int, Int> {

    override fun map(value: Int): Int {
        return value * factor
    }

    override val expression = "x -> x * $factor"
}