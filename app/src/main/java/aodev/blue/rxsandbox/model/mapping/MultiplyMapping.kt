package aodev.blue.rxsandbox.model.mapping


class MultiplyMapping : Mapping<Int, Int> {

    override fun map(value: Int): Int {
        return value * 2
    }
}