package aodev.blue.rxsandbox.utils

import java.util.*


fun <T> linkedListOf(vararg elements: T): LinkedList<T> {
    return LinkedList(elements.toList())
}

fun <T> linkedListOf(elements: List<T>): LinkedList<T> {
    return LinkedList(elements)
}