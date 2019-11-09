package aodev.blue.rxsandbox.utils

import java.util.*


fun <T> linkedListOf(vararg elements: T): LinkedList<T> {
    return LinkedList(elements.toList())
}

fun <T> linkedListOf(elements: List<T>): LinkedList<T> {
    return LinkedList(elements)
}

fun <T> List<T>.toLinkedList(): LinkedList<T> {
    return LinkedList(this)
}

fun <T> List<T>.alter(index: Int, newValue: T): List<T> {
    if (index < 0 || index >= size) {
        throw IndexOutOfBoundsException("Collection doesn't contain element at index $index.")
    }

    return mapIndexed { i, oldValue ->
        if (i == index) newValue else oldValue
    }
}

/**
 * Return the index of the first element yielding the smallest value of the given function or
 * `-1` if there are no elements.
 */
inline fun <T, R : Comparable<R>> Iterable<T>.indexOfMinBy(selector: (T) -> R): Int {
    val iterator = iterator()
    if (!iterator.hasNext()) return -1

    val firstElem = iterator.next()
    var minIndex = 0
    var index = 0
    if (!iterator.hasNext()) return minIndex
    var minValue = selector(firstElem)

    do {
        val e = iterator.next()
        index++

        val v = selector(e)
        if (minValue > v) {
            minIndex = index
            minValue = v
        }
    } while (iterator.hasNext())

    return minIndex
}

/**
 * Returns a list of values built from the elements of the [lists] with the same index
 * using the provided [transform] function applied to each list of elements.
 * The returned list has length of the shortest collection.
 */
inline fun <T, V> zip(vararg lists: List<T>, transform: (List<T>) -> V): List<V> {
    val minSize = lists.map(List<T>::size).min() ?: return emptyList()
    val list = ArrayList<V>(minSize)

    val iterators = lists.map { it.iterator() }
    repeat(minSize) {
        list.add(transform(iterators.map { it.next() }))
    }

    return list
}
