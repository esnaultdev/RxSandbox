package aodev.blue.rxsandbox.ui.widget.asynctree

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import aodev.blue.rxsandbox.model.AsyncTree
import aodev.blue.rxsandbox.model.CompletableT
import aodev.blue.rxsandbox.model.MaybeT
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.ui.widget.timeline.CompletableTimelineView
import aodev.blue.rxsandbox.ui.widget.timeline.MaybeTimelineView
import aodev.blue.rxsandbox.ui.widget.timeline.ObservableTimelineView
import aodev.blue.rxsandbox.ui.widget.timeline.SingleTimelineView
import io.reactivex.Observable
import io.reactivex.rxkotlin.cast
import kotlin.properties.Delegates


class AsyncTreeView : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    private val constraintSet: ConstraintSet = ConstraintSet().also {
        it.clone(this)
    }

    var asyncTree: AsyncTree<Int>? by Delegates.observable<AsyncTree<Int>?>(null) {
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

    private fun updateViews(asyncTree: AsyncTree<Int>) {
        val rootTimeline = asyncTree.timeline()
        val (timelineView, _) = createViewForTimeline(context, rootTimeline, true)
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

    private fun createViewForTimeline(
            context: Context,
            timeline: Timeline<Int>,
            readOnly: Boolean
    ): Pair<View, Observable<Timeline<Int>>> {
        return when (timeline) {
            is ObservableT -> {
                val timelineView = ObservableTimelineView(context)
                timelineView.timeline = timeline
                timelineView.readOnly = readOnly
                timelineView to timelineView.timelineObservable.cast()
            }
            is SingleT -> {
                val timelineView = SingleTimelineView(context)
                timelineView.timeline = timeline
                timelineView.readOnly = readOnly
                timelineView to timelineView.timelineObservable.cast()
            }
            is MaybeT -> {
                val timelineView = MaybeTimelineView(context)
                timelineView.timeline = timeline
                timelineView.readOnly = readOnly
                timelineView to timelineView.timelineObservable.cast()
            }
            is CompletableT -> {
                val timelineView = CompletableTimelineView(context)
                timelineView.timeline = timeline
                timelineView.readOnly = readOnly
                timelineView to timelineView.timelineObservable.cast()
            }
        }
    }
}