package aodev.blue.rxsandbox.model.operations.mapping


interface Mapping<in T, out R> {

    fun map(value: T): R

    fun expression(): String
}