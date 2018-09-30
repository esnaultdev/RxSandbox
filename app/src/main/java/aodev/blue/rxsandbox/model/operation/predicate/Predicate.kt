package aodev.blue.rxsandbox.model.operation.predicate


interface Predicate<in T> {

    fun check(value: T): Boolean

    fun expression(): String
}