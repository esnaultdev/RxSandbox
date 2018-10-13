package aodev.blue.rxsandbox.utils

import java.util.*


fun <T> linkedListOf(vararg elements: T): LinkedList<T> {
    return LinkedList(elements.toList())
}

fun <T> linkedListOf(elements: List<T>): LinkedList<T> {
    return LinkedList(elements)
}

fun <T> List<T>.alter(index: Int, newValue: T): List<T> {
    if (index < 0 || index >= size) {
        throw IndexOutOfBoundsException("Collection doesn't contain element at index $index.")
    }

    return mapIndexed { i, oldValue ->
        if (i == index) newValue else oldValue
    }
}
