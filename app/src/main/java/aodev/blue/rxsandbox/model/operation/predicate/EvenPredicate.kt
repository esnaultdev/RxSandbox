package aodev.blue.rxsandbox.model.operation.predicate


class EvenPredicate : Predicate<Int> {

    override fun check(value: Int): Boolean {
        return value % 2 == 0
    }

    override val expression = "x -> x % 2 == 0"
}