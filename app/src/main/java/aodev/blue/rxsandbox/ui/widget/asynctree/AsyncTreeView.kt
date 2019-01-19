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

    var asyncTree: ReactiveTypeX<*, *>? by Delegates.observable<ReactiveTypeX<*, *>?>(null) {
        _, oldValue, newValue ->
        if (oldValue != newValue) {
            updateViews()
        }
    }

    private fun updateViews() {
        val asyncTree = asyncTree
        removeAllViews()
        if (asyncTree != null) {
            updateViews(asyncTree)
        }
    }

    private fun updateViews(asyncTree: ReactiveTypeX<*, *>) {
        var previousViewId: Int? = null

        val lastTimeline = asyncTree.innerX.timeline()
        val currentView = when (lastTimeline) {
            is ObservableT -> ObservableTimelineView(context)
            is SingleT -> SingleTimelineView(context)
            is MaybeT -> MaybeTimelineView(context)
            is CompletableT -> MaybeTimelineView(context)
        }
        currentView.id = View.generateViewId()
        addView(currentView)

        constraintSet.run {
            constrainWidth(currentView.id, ConstraintSet.MATCH_CONSTRAINT)
            constrainHeight(currentView.id, ConstraintSet.WRAP_CONTENT)
            connect(currentView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(currentView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

            val previousId = previousViewId
            if (previousId == null) {
                connect(currentView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            } else {
                connect(currentView.id, ConstraintSet.BOTTOM, previousId, ConstraintSet.TOP)
            }
        }
        previousViewId = currentView.id

        constraintSet.applyTo(this)
    }
}