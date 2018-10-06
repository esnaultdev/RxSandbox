package aodev.blue.rxsandbox.model.operation.mapping


interface Mapping<in T, out R> {

    fun map(value: T): R

    val expression: String
}