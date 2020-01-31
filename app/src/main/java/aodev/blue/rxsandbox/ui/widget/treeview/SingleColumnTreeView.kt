package aodev.blue.rxsandbox.ui.widget.treeview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import aodev.blue.rxsandbox.model.*
import aodev.blue.rxsandbox.ui.widget.timeline.TimelineView
import aodev.blue.rxsandbox.ui.widget.treeview.model.TreeViewModel as ViewModel
import aodev.blue.rxsandbox.ui.widget.treeview.model.TreeViewState as ViewState
import aodev.blue.rxsandbox.ui.widget.treeview.model.buildViewState
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
    private val updater = TreeUpdater(this::updateViews)

    private fun updateViews() {
        val reactiveTypeX = reactiveTypeX

        removeAllViews()
        updater.viewState = null

        if (reactiveTypeX != null) {
            viewState = buildViewState(reactiveTypeX)
            updater.viewState = viewState
        }
    }

    /* *****************************************************************************************************************/
    //region Display **************************************************************************/

    private fun updateViews(viewModel: ViewModel) {
        removeAllViews()
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
            when (val type = element.type) {
                is ViewModel.Element.TimelineE.Type.Input -> {
                    readOnly = false
                    onUpdate = { timeline -> timeline?.let(type.onUpdate) }
                }
                is ViewModel.Element.TimelineE.Type.Result -> {
                    readOnly = true
                    onUpdate = {}
                }
            }

            downConnection = element.downConnection
            selection = element.selection
            onSelected = element.onSelected

            element.update = { timeline -> this.timeline = timeline}
        }
    }

    //endregion
}
