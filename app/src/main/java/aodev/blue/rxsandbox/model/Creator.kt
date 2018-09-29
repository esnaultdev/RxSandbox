package aodev.blue.rxsandbox.model


interface Creator<out R> : Operator<Unit, R> {

    override fun apply(input: Unit): R = create()

    fun create(): R
}