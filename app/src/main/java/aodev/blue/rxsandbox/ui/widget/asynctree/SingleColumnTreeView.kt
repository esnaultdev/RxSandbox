package aodev.blue.rxsandbox.ui.widget.asynctree

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import aodev.blue.rxsandbox.model.*
import aodev.blue.rxsandbox.ui.widget.timeline.TimelineView
import aodev.blue.rxsandbox.utils.linkedListOf
import java.util.*
import kotlin.properties.Delegates
import kotlinx.coroutines.*
import kotlin.reflect.KMutableProperty0


class SingleColumnTreeView : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    private val constraintSet: ConstraintSet = ConstraintSet().also {
        it.clone(this)
    }

    var reactiveTypeX: ReactiveTypeX<*, *>? by Delegates.observable<ReactiveTypeX<*, *>?>(null) {
        _, oldValue, newValue ->
        if (oldValue != newValue) {
            updateViews()
        }
    }

    private var viewState: ViewState? = null
    private var viewModel: ViewModel? = null
        set(value) {
            updater.viewModel = value
            field = value
        }
    private val updater = Updater()

    private fun updateViews() {
        val reactiveTypeX = reactiveTypeX

        removeAllViews()
        viewModel = null

        if (reactiveTypeX != null) {
            updateViewState(reactiveTypeX)
            updateViewModel()
            viewModel?.let(this::updateViews)
        }
    }

    /* *****************************************************************************************************************/
    //region Display **************************************************************************/

    private fun updateViews(viewModel: ViewModel) {
        val viewIds = IntArray(viewModel.elements.size)

        viewModel.elements.forEachIndexed { index, element ->
            val currentView = when (element) {
                is ViewModel.Element.Operator -> {
                    OperatorView(context).apply {
                        text = element.name
                        docUrl = element.docUrl
                    }
                }
                is ViewModel.Element.TimelineE -> bindElementToView(element)
            }

            currentView.id = View.generateViewId()
            addView(currentView)
            viewIds[index] = currentView.id

            constraintSet.run {
                constrainWidth(currentView.id, ConstraintSet.MATCH_CONSTRAINT)
                constrainHeight(currentView.id, ConstraintSet.WRAP_CONTENT)
                connect(currentView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                connect(currentView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            }
        }

        if (viewModel.elements.isNotEmpty()) {
            constraintSet.createVerticalChain(
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM,
                    viewIds,
                    FloatArray(viewModel.elements.size) { 1f },
                    ConstraintSet.CHAIN_PACKED
            )
        }

        constraintSet.applyTo(this)
    }

    private fun bindElementToView(element: ViewModel.Element.TimelineE): View {
        return TimelineView(context).apply {
            when (element) {
                is ViewModel.Element.TimelineE.Input -> {
                    readOnly = false
                    onUpdate = { timeline -> timeline?.let(element.onUpdate) }
                }
                is ViewModel.Element.TimelineE.Result -> {
                    readOnly = true
                    onUpdate = {}
                }
            }

            element.update = { timeline -> this.timeline = timeline}
        }
    }

    //endregion

    /* *****************************************************************************************************************/
    //region View state **************************************************************************/

    private fun updateViewState(reactiveTypeX: ReactiveTypeX<*, *>) {
        viewState = ViewState(buildElement(reactiveTypeX, true))
    }

    // static
    private fun <T : Any, TL : Timeline<T>> buildElement(
            reactiveTypeX: ReactiveTypeX<T, TL>,
            selected: Boolean
    ): ViewState.Element<*, *> {
        val element = ViewState.Element(reactiveTypeX)

        val innerX = reactiveTypeX.innerX
        if (selected && innerX is InnerReactiveTypeX.Result<T, TL>) {
            buildPrevious(innerX, element)
        }

        return element
    }

    // static
    private fun <T : Any, TL : Timeline<T>> buildPrevious(
            innerX: InnerReactiveTypeX.Result<T, TL>,
            element: ViewState.Element<*, *>
    ) {
        if (innerX.previous.isEmpty()) return

        innerX.previous.forEachIndexed { index, typeX ->
            val selected = element.selectedPreviousIndex == index
            val previousElement = buildElement(typeX, selected)

            previousElement.next = element
            element.previous[index] = previousElement
        }
    }

    class ViewState(val bottomElement: Element<*, *>) {

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

    //endregion

    /* *****************************************************************************************************************/
    //region View model **************************************************************************/

    private fun updateViewModel() {
        viewModel = viewState?.let { buildViewModel(it) }
    }

    // static
    private fun buildViewModel(viewState: ViewState): ViewModel {
        // We use a linked list to be able to add elements in the first place in O(1)
        val viewElements = linkedListOf<ViewModel.Element>()
        addTimelineToVMElements(viewState.bottomElement, viewElements)
        addUpstreamToVMElements(viewState.bottomElement, viewElements)
        return ViewModel(viewElements)
    }

    // static
    /**
     * Add the timeline of an element to the [ViewModel] elements.
     */
    private fun <T : Any, TL : Timeline<T>> addTimelineToVMElements(
            stateElement: ViewState.Element<T, TL>,
            viewElements: LinkedList<ViewModel.Element>
    ) {
        when (val innerX = stateElement.reactiveTypeX.innerX) {
            is InnerReactiveTypeX.Input -> {
                val onUpdate = fun (timeline: Timeline<Any>) {
                    // Update the typeX timeline
                    @Suppress("UNCHECKED_CAST") // TODO Make this unchecked cast unnecessary
                    (timeline as? TL)?.let { innerX.input = it }

                    // Invalidate the elements depending on it
                    stateElement.next?.invalidate()

                    // Update the timelines
                    updater.updateTimelines()
                }
                ViewModel.Element.TimelineE.Input(
                        result = innerX::input::get,
                        shown = stateElement::shown,
                        onUpdate = onUpdate
                ).also {
                    viewElements.add(0, it)
                }
            }
            is InnerReactiveTypeX.Result -> {
                ViewModel.Element.TimelineE.Result(
                        result = innerX::result,
                        shown = stateElement::shown
                ).also {
                    viewElements.add(0, it)
                }
            }
        }
    }

    // static
    /**
     * Add the upstream of an element to the [ViewModel] elements.
     * The upstream is composed of the operator, the previous timelines and the upstream of the
     * selected element, recursively.
     */
    private fun <T : Any, TL: Timeline<T>> addUpstreamToVMElements(
            stateElement: ViewState.Element<T, TL>,
            viewElements: LinkedList<ViewModel.Element>
    ) {
        val innerX = stateElement.reactiveTypeX.innerX as? InnerReactiveTypeX.Result<T, TL> ?: return

        // Add the operator
        ViewModel.Element.Operator(innerX.operator.expression, innerX.operator.docUrl).also {
            viewElements.add(0, it)
        }

        // Add each previous as a timeline
        stateElement.previous.reversed()
                .filterNotNull()
                .forEach { addTimelineToVMElements(it, viewElements) }

        // Add the upstream of the selected previous
        stateElement.previous.getOrNull(stateElement.selectedPreviousIndex)
                ?.let { this.addUpstreamToVMElements(it, viewElements) }
    }

    class ViewModel(val elements: List<Element>) {

        sealed class Element {
            sealed class TimelineE(
                    val result: () -> Timeline<Any>,
                    val shown: KMutableProperty0<Boolean>
            ) : Element() {

                var update: (Timeline<Any>) -> Unit = {}

                class Input(
                        result: () -> Timeline<Any>,
                        shown: KMutableProperty0<Boolean>,
                        val onUpdate: (Timeline<Any>) -> Unit
                ) : TimelineE(result, shown)

                class Result(
                        result: () -> Timeline<Any>,
                        shown: KMutableProperty0<Boolean>
                ) : TimelineE(result, shown)
            }

            class Operator(
                    val name: String,
                    val docUrl: String?
            ) : Element()
        }
    }

    //endregion

    /* *****************************************************************************************************************/
    //region Timeline updates **************************************************************************/

    private class Updater {

        var viewModel: ViewModel? = null
            set(value) {
                field = value
                cancel()
                updateTimelines()
            }

        private var job: Job? = null

        fun updateTimelines() {
            job?.cancel()

            val viewModel = viewModel ?: return
            job = GlobalScope.launch {
                updateTimelinesAsync(viewModel)
            }
        }

        private fun CoroutineScope.updateTimelinesAsync(viewModel: ViewModel) {
            viewModel.elements.forEach { element ->
                if (element is ViewModel.Element.TimelineE && !element.shown.get()) {
                    val result = element.result()
                    launch(Dispatchers.Main) {
                        element.update(result)
                        element.shown.set(true)
                    }
                }
            }
        }

        private fun cancel() {
            job?.cancel()
            job = null
        }
    }

    //endregion
}
