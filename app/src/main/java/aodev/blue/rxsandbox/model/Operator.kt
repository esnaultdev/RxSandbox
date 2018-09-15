package aodev.blue.rxsandbox.model


interface Operator<in T, out R>  {

    fun apply(input: T): R

    fun expression(): String
}