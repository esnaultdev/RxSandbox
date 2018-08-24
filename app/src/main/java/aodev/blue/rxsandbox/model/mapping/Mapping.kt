package aodev.blue.rxsandbox.model.mapping


interface Mapping<in T, out R> {

    fun map(value: T): R
}