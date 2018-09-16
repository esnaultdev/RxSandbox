package aodev.blue.rxsandbox.model



interface Creator<out T> {

    fun create(): T

    fun expression(): String
}