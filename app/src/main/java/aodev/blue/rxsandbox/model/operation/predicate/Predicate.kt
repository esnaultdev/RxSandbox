package aodev.blue.rxsandbox.model.operation.predicate


interface Predicate<in T> {

    fun check(value: T): Boolean

    val expression: String
}