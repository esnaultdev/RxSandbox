package aodev.blue.rxsandbox.ui.widget.asynctree

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import aodev.blue.rxsandbox.model.CompletableT
import aodev.blue.rxsandbox.model.MaybeT
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.ReactiveTypeX
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.ui.widget.timeline.TimelineView
import kotlin.properties.Delegates
import kotlin.reflect.KClass


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

    private fun updateViews() {
        val reactiveTypeX = reactiveTypeX
        removeAllViews()
        if (reactiveTypeX != null) {
            val viewModel = toViewModel(reactiveTypeX)
            updateViews(viewModel)
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
                is ViewModel.Element.TimelineE<*, *> -> bindElementToView(element)
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

    private fun bindElementToView(element: ViewModel.Element.TimelineE<*, *>): View {
        return TimelineView(context).apply {
            readOnly = element.inner.isInput

            // onUpdate = element.inner.onUpdate
            element.inner.update = { timeline -> this.timeline = timeline}
        }
    }

    private fun toViewModel(reactiveTypeX: ReactiveTypeX<*, *>): ViewModel {
        return ViewModel(
                listOf(
                        ViewModel.Element.TimelineE.Single(
                                ViewModel.Element.TimelineE.Inner(true, {}), Int::class
                        ),
                        ViewModel.Element.Operator("map"),
                        ViewModel.Element.TimelineE.Single(
                                ViewModel.Element.TimelineE.Inner(false, {}), Int::class
                        ),
                        ViewModel.Element.Operator("map"),
                        ViewModel.Element.TimelineE.Single(
                                ViewModel.Element.TimelineE.Inner(false, {}), Int::class
                        )
                )
        )
    }

    class ViewModel(val elements: List<Element>) {

        sealed class Element {
            sealed class TimelineE<T: Any, TL : Timeline<T>>(
                    val inner: Inner<T, TL>, val type: KClass<T>
            ) : Element() {

                class Inner<out T : Any, TL : Timeline<T>>(
                        val isInput: Boolean
                        // val onUpdate: (TL) -> Unit
                ) {
                    var update: (TL) -> Unit = {}
                }

                class Observable<T: Any>(
                        inner: Inner<T, ObservableT<T>>, type: KClass<T>
                ) : TimelineE<T, ObservableT<T>>(inner, type)

                class Single<T: Any>(
                        inner: Inner<T, SingleT<T>>, type: KClass<T>
                ) : TimelineE<T, SingleT<T>>(inner, type)

                class Maybe<T: Any>(
                        inner: Inner<T, MaybeT<T>>, type: KClass<T>
                ) : TimelineE<T, MaybeT<T>>(inner, type)

                class Completable(
                        inner: Inner<Nothing, CompletableT>
                ) : TimelineE<Nothing, CompletableT>(inner, Nothing::class)
            }

            class Operator(val name: String) : Element()
        }
    }
}
