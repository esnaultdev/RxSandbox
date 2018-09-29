package aodev.blue.rxsandbox.model.operations.mapping


class MultiplyMapping(private val factor: Int) : Mapping<Int, Int> {

    override fun map(value: Int): Int {
        return value * factor
    }

    override fun expression(): String {
        return "x -> x * $factor"
    }
}