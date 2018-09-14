package aodev.blue.rxsandbox.model.operations.predicate


class EvenPredicate : Predicate<Int> {

    override fun check(value: Int): Boolean {
        return value % 2 == 0
    }

    override fun expression(): String {
        return "x -> x % 2 == 0"
    }
}