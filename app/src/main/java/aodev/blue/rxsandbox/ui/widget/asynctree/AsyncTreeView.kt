package aodev.blue.rxsandbox.ui.widget.asynctree

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import aodev.blue.rxsandbox.model.AsyncTree
import aodev.blue.rxsandbox.model.CompletableT
import aodev.blue.rxsandbox.model.CompletableX
import aodev.blue.rxsandbox.model.MaybeT
import aodev.blue.rxsandbox.model.MaybeX
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.ObservableX
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.SingleX
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.Operator
import aodev.blue.rxsandbox.ui.widget.timeline.CompletableTimelineView
import aodev.blue.rxsandbox.ui.widget.timeline.MaybeTimelineView
import aodev.blue.rxsandbox.ui.widget.timeline.ObservableTimelineView
import aodev.blue.rxsandbox.ui.widget.timeline.SingleTimelineView
import kotlin.properties.Delegates


class AsyncTreeView : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    private val constraintSet: ConstraintSet = ConstraintSet().also {
        it.clone(this)
    }

    var asyncTree: AsyncTree<*>? by Delegates.observable<AsyncTree<*>?>(null) {
        _, _, _ ->
        updateViews()
    }

    private fun updateViews() {
        val asyncTree = asyncTree
        if (asyncTree == null) {
            removeAllViews()
        } else {
            updateViews(asyncTree)
        }
    }

    private fun updateViews(asyncTree: AsyncTree<*>) {
        val timelineView = when (asyncTree) {
            is ObservableX -> {
                val observableT = asyncTree.observableT()
                when (asyncTree) {
                    is ObservableX.Input -> createObservableTimelineView(observableT, false)
                    is ObservableX.Result -> createObservableTimelineView(observableT, true)
                }
            }
            is SingleX -> {
                val singleT = asyncTree.singleT()
                when (asyncTree) {
                    is SingleX.Input -> createSingleTimelineView(singleT, false)
                    is SingleX.Result -> createSingleTimelineView(singleT, true)
                }
            }
            is MaybeX -> {
                val maybeT = asyncTree.maybeT()
                when (asyncTree) {
                    is MaybeX.Input -> createMaybeTimelineView(maybeT, false)
                    is MaybeX.Result -> createMaybeTimelineView(maybeT, true)
                }
            }
            is CompletableX -> {
                val completableT = asyncTree.completableT()
                when (asyncTree) {
                    is CompletableX.Input -> createCompletableTimelineView(completableT, false)
                    is CompletableX.Result -> createCompletableTimelineView(completableT, true)
                }
            }
        }

        // If it's an Input:
        // We've reached the top!
        // Add the view, connect it to the top (if it's the first view) and connect the previous view to it

        // If it's a Result:
        // Add the view and connect the previous view to it
        // Add the operator view and connect the timeline view to it
        // Add the previous timelines if selected

        timelineView.id = View.generateViewId()

        addView(timelineView)
        constraintSet.run {
            constrainWidth(timelineView.id, ConstraintSet.MATCH_CONSTRAINT)
            constrainHeight(timelineView.id, ConstraintSet.WRAP_CONTENT)
            connect(timelineView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(timelineView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            connect(timelineView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        }

        constraintSet.applyTo(this)
    }

    private fun setupViewModel(asyncTree: AsyncTree<*>): ViewModel {

    }

    private fun getRowViewModel(asyncTree: List<AsyncTree<*>>): Pair<ViewModel, List<AsyncTree<*>>> {
        return when (asyncTree) {
            is ObservableX -> {
                when (asyncTree) {
                    is ObservableX.Input -> ViewModel.DisplayTimeline.Loading to emptyList()
                    is ObservableX.Result -> ViewModel.DisplayTimeline.Loading to listOf
                }
            }
            is SingleX -> {
                when (asyncTree) {
                    is SingleX.Input -> ViewModel.DisplayTimeline.Loading to emptyList()
                    is SingleX.Result -> ViewModel.DisplayTimeline.Loading to emptyList()
                }
            }
            is MaybeX -> {
                when (asyncTree) {
                    is MaybeX.Input -> ViewModel.DisplayTimeline.Loading to emptyList()
                    is MaybeX.Result -> ViewModel.DisplayTimeline.Loading to emptyList()
                }
            }
            is CompletableX -> {
                when (asyncTree) {
                    is CompletableX.Input -> ViewModel.DisplayTimeline.Loading to emptyList()
                    is CompletableX.Result -> ViewModel.DisplayTimeline.Loading to emptyList()
                }
            }
        }
    }

    data class ViewModel(
            val rows: List<Row>
    ) {

        data class Row(
                val topOperator: Operator?,
                val timelines: List<DisplayTimeline>,
                val selectedIndex: Int
        )

        sealed class DisplayTimeline {
            object Loading : DisplayTimeline()
            object NonDisplayable : DisplayTimeline()
            class Displayable<T : Timeline<Int>>(
                    val timeline: T,
                    val onChange: ((T) -> Unit)? // null if can't interact
            ) : DisplayTimeline()
        }
    }

    private fun createObservableTimelineView(
            observableT: ObservableT<Int>,
            readOnly: Boolean
    ): ObservableTimelineView {
        return ObservableTimelineView(context).apply {
            this.timeline = observableT
            this.readOnly = readOnly
        }
    }

    private fun createSingleTimelineView(
            singleT: SingleT<Int>,
            readOnly: Boolean
    ): SingleTimelineView {
        return SingleTimelineView(context).apply {
            this.timeline = singleT
            this.readOnly = readOnly
        }
    }

    private fun createMaybeTimelineView(
            maybeT: MaybeT<Int>,
            readOnly: Boolean
    ): MaybeTimelineView {
        return MaybeTimelineView(context).apply {
            this.timeline = maybeT
            this.readOnly = readOnly
        }
    }

    private fun createCompletableTimelineView(
            completableT: CompletableT,
            readOnly: Boolean
    ): CompletableTimelineView {
        return CompletableTimelineView(context).apply {
            this.timeline = completableT
            this.readOnly = readOnly
        }
    }
}