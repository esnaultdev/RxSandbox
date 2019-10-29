package aodev.blue.rxsandbox.ui.widget.asynctree

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import aodev.blue.rxsandbox.model.*
import aodev.blue.rxsandbox.ui.widget.timeline.TimelineView
import kotlin.properties.Delegates


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

    private fun updateViews() {
        val reactiveTypeX = reactiveTypeX
        removeAllViews()
        if (reactiveTypeX != null) {
            updateViewState(reactiveTypeX)
            // TODO build and show the view model
        } else {
            // TODO clean the state and the updates
        }
    }

    private fun updateViews(viewModel: ViewModel) {
        val viewIds = IntArray(viewModel.elements.size)

        viewModel.elements.forEachIndexed { index, element ->
            val currentView = when (element) {
                is ViewModel.Element.Operator -> {
                    OperatorView(context).apply {
                        text = element.name
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
            readOnly = element.onUpdate == null

            onUpdate = { timeline -> timeline?.let { element.onUpdate?.invoke(it) } }
            element.update = { timeline -> this.timeline = timeline}
        }
    }

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

    class ViewModel(val elements: List<Element>) {

        sealed class Element {
            class TimelineE(
                    // If null the timeline is not used as an input
                    val onUpdate: ((Timeline<Any>) -> Unit)?
            ) : Element() {
                var update: (Timeline<Any>) -> Unit = {}
            }

            class Operator(
                    val name: String,
                    val docUrl: String?
            ) : Element()
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
             * Invalidate this element and all the elements depending on it.
             */
            fun invalidate() {
                if (reactiveTypeX is InnerReactiveTypeX.Result<*, *>) {
                    reactiveTypeX.invalidate()
                }
                next?.invalidate()
            }
        }
    }
}
