package aodev.blue.rxsandbox.ui.widget.treeview.model

import aodev.blue.rxsandbox.model.InnerReactiveTypeX
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.ui.widget.model.TimelineConnection
import aodev.blue.rxsandbox.ui.widget.model.TimelineSelection
import aodev.blue.rxsandbox.utils.linkedListOf
import java.util.*
import kotlin.reflect.KMutableProperty0


/**
 * The ViewModel of the tree that the UI can bind to in a MVVM fashion.
 */
class TreeViewModel(val elements: List<Element>) {

    sealed class Element {
        class TimelineE(
                val result: () -> Timeline<Any>,
                val shown: KMutableProperty0<Boolean>,
                val downConnection: TimelineConnection,
                var selection: TimelineSelection,
                val type: Type
        ) : Element() {

            var update: (Timeline<Any>) -> Unit = {}

            sealed class Type {
                class Input(val onUpdate: (Timeline<Any>) -> Unit) : Type()
                object Result : Type()
            }
        }

        class Operator(
                val name: String,
                val docUrl: String?
        ) : Element()
    }
}


/**
 * Build a new [TreeViewModel] based on the [TreeViewState].
 */
fun buildViewModel(viewState: TreeViewState, onUpdateTimelines: () -> Unit): TreeViewModel {
    // We use a linked list to be able to add elements at the first index in O(1)
    val viewElements = linkedListOf<TreeViewModel.Element>()

    // Add the bottom element
    addTimelineToVMElements(
            stateElement = viewState.bottomElement,
            onUpdateTimelines = onUpdateTimelines,
            selection = TimelineSelection.Alone,
            downConnection = TimelineConnection.NONE,
            viewElements = viewElements
    )
    addUpstreamToVMElements(viewState.bottomElement, onUpdateTimelines, viewElements)

    return TreeViewModel(viewElements)
}


/**
 * Add the timeline of an element to the [TreeViewModel] elements.
 */
private fun <T : Any, TL : Timeline<T>> addTimelineToVMElements(
        stateElement: TreeViewState.Element<T, TL>,
        onUpdateTimelines: () -> Unit,
        selection: TimelineSelection,
        downConnection: TimelineConnection,
        viewElements: LinkedList<TreeViewModel.Element>
) {
    val viewElement = when (val innerX = stateElement.reactiveTypeX.innerX) {
        is InnerReactiveTypeX.Input -> {
            val onUpdate = fun (timeline: Timeline<Any>) {
                // Update the typeX timeline
                @Suppress("UNCHECKED_CAST") // TODO Make this unchecked cast unnecessary
                (timeline as? TL)?.let { innerX.input = it }

                // Invalidate the elements depending on it
                stateElement.next?.invalidate()

                // Compute the new results for the dependent timelines
                onUpdateTimelines()
            }
            TreeViewModel.Element.TimelineE(
                    result = innerX::input::get,
                    shown = stateElement::shown,
                    downConnection = downConnection,
                    selection = selection,
                    type = TreeViewModel.Element.TimelineE.Type.Input(onUpdate)
            )
        }
        is InnerReactiveTypeX.Result -> {
            TreeViewModel.Element.TimelineE(
                    result = innerX::result,
                    shown = stateElement::shown,
                    downConnection = downConnection,
                    selection = selection,
                    type = TreeViewModel.Element.TimelineE.Type.Result
            )
        }
    }
    viewElements.add(0, viewElement)
}


/**
 * Add the upstream of an element to the [ViewModel] elements.
 * The upstream is composed of the operator, the previous timelines and the upstream of the
 * selected element, recursively.
 */
private fun <T : Any, TL: Timeline<T>> addUpstreamToVMElements(
        stateElement: TreeViewState.Element<T, TL>,
        onUpdateTimelines: () -> Unit,
        viewElements: LinkedList<TreeViewModel.Element>
) {
    val innerX = stateElement.reactiveTypeX.innerX as? InnerReactiveTypeX.Result<T, TL> ?: return

    // Add the operator
    TreeViewModel.Element.Operator(innerX.operator.expression, innerX.operator.docUrl).also {
        viewElements.add(0, it)
    }

    // Add each previous as a timeline
    val selectedIsResult = stateElement.previous.getOrNull(stateElement.selectedPreviousIndex)
            ?.reactiveTypeX
            ?.innerX
            ?.let { it is InnerReactiveTypeX.Result<*, *> }
            ?: false

    val reversedSelectedIndex = stateElement.previous.lastIndex - stateElement.selectedPreviousIndex
    val onePrevious = stateElement.previous.size == 1

    // Iterate in reverse order to add the timeline views in the right order
    stateElement.previous.reversed()
            .forEachIndexed { index, element ->
                if (element == null) return@forEachIndexed

                val selection = when {
                    onePrevious && selectedIsResult -> TimelineSelection.Alone
                    onePrevious && !selectedIsResult -> TimelineSelection.None
                    else -> {
                        val selected = index == reversedSelectedIndex
                        val connected = (selected && selectedIsResult)
                                || (!selected && selectedIsResult && index > reversedSelectedIndex)
                        TimelineSelection.Checkbox(selected, connected)
                    }
                }

                val downConnection = if (index == stateElement.previous.lastIndex) {
                    TimelineConnection.TOP
                } else {
                    TimelineConnection.MIDDLE
                }

                addTimelineToVMElements(element, onUpdateTimelines, selection, downConnection, viewElements)
            }

    // Add the upstream of the selected previous
    stateElement.previous.getOrNull(stateElement.selectedPreviousIndex)
            ?.let { addUpstreamToVMElements(it, onUpdateTimelines, viewElements) }
}
