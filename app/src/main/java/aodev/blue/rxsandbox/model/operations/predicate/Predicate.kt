package aodev.blue.rxsandbox.model.operations.predicate


interface Predicate<in T> {

    fun check(value: T): Boolean

    fun expression(): String
}