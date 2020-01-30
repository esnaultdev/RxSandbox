package aodev.blue.rxsandbox.ui.widget.treeview.model

import aodev.blue.rxsandbox.model.InnerReactiveTypeX
import aodev.blue.rxsandbox.model.ReactiveTypeX
import aodev.blue.rxsandbox.model.Timeline


/**
 * The state of the displayed tree.
 * The vie
 */
class TreeViewState(val bottomElement: Element<*, *>) {

    class Element<out T : Any, out TL : Timeline<T>>(
            val reactiveTypeX: ReactiveTypeX<T, TL>
    ) {
        /**
         * The next element.
         */
        var next: Element<*, *>? = null

        /**
         * The list of previous elements.
         * We only build the previous elements when needed.
         */
        var previous: MutableList<Element<*, *>?> = when (val innerX = reactiveTypeX.innerX) {
            is InnerReactiveTypeX.Input<*, *> -> mutableListOf()
            is InnerReactiveTypeX.Result<*, *> -> MutableList(innerX.previous.size) { null }
        }

        /**
         * The index of the selected [previous] element.
         */
        var selectedPreviousIndex: Int = 0

        /**
         * True if the last computed timeline has been shown.
         */
        var shown: Boolean = false

        /**
         * Invalidate this element and all the elements depending on it.
         */
        fun invalidate() {
            shown = false

            val innerX = reactiveTypeX.innerX
            if (innerX is InnerReactiveTypeX.Result<*, *>) {
                innerX.invalidate()
            }

            next?.invalidate()
        }
    }
}


/**
 * Build a new [TreeViewState] based on the [reactiveTypeX].
 */
fun buildViewState(reactiveTypeX: ReactiveTypeX<*, *>): TreeViewState {
    return TreeViewState(buildElement(reactiveTypeX, true))
}

/**
 * Build a [TreeViewState.Element] by recursively building the element and its previous.
 *
 * The recursion uses [buildElement] to build an element which calls [buildPrevious] to build
 * all the previous elements, which calls [buildElement] etc.
 */
private fun <T : Any, TL : Timeline<T>> buildElement(
        reactiveTypeX: ReactiveTypeX<T, TL>,
        selected: Boolean
): TreeViewState.Element<*, *> {
    val element = TreeViewState.Element(reactiveTypeX)

    val innerX = reactiveTypeX.innerX
    // We can be lazy and only build the previous of the selected element.
    // The other ones don't need to be displayed and are not needed to compute the result timelines.
    if (selected && innerX is InnerReactiveTypeX.Result<T, TL>) {
        buildPrevious(innerX, element)
    }

    return element
}

/**
 * Build the previous elements of a [TreeViewState.Element] recursively.
 *
 * The recursion uses [buildElement] to build an element which calls [buildPrevious] to build
 * all the previous elements, which calls [buildElement] etc.
 */
private fun <T : Any, TL : Timeline<T>> buildPrevious(
        innerX: InnerReactiveTypeX.Result<T, TL>,
        element: TreeViewState.Element<*, *>
) {
    if (innerX.previous.isEmpty()) return

    innerX.previous.forEachIndexed { index, typeX ->
        // Build the previous element
        val selected = element.selectedPreviousIndex == index
        val previousElement = buildElement(typeX, selected)

        // Setup the previous and next references
        previousElement.next = element
        element.previous[index] = previousElement
    }
}
