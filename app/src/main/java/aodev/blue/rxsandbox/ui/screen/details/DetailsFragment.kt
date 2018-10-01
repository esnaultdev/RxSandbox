package aodev.blue.rxsandbox.ui.screen.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.CompletableT
import aodev.blue.rxsandbox.model.MaybeT
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.sample.OperatorSample
import aodev.blue.rxsandbox.model.sample.getObservableSample
import aodev.blue.rxsandbox.ui.screen.NavigationLabelListener
import aodev.blue.rxsandbox.ui.widget.operator.OperatorView
import aodev.blue.rxsandbox.ui.widget.timeline.CompletableTimelineView
import aodev.blue.rxsandbox.ui.widget.timeline.MaybeTimelineView
import aodev.blue.rxsandbox.ui.widget.timeline.ObservableTimelineView
import aodev.blue.rxsandbox.ui.widget.timeline.SingleTimelineView


class DetailsFragment : Fragment() {

    private val operatorName: String by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getString("operator_name") ?: ""
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? NavigationLabelListener)?.updateLabel(operatorName)

        val sample = getObservableSample(operatorName)
        if (sample != null) {
            setupViews(view, sample)
        } else {
            TODO("Properly handle a missing operator")
        }
    }

    private fun setupViews(view: View, sample: OperatorSample) {
        val operatorView: OperatorView = view.findViewById(R.id.details_operator)
        operatorView.text = sample.operator.expression()

        val rootContainer: ConstraintLayout = view.findViewById(R.id.details_root)
        if (sample.input.isNotEmpty()) {
            // Currently just display the first timeline for simplicity
            val timeline: Timeline<Int> = sample.input.first()

            val (timelineView, timelineObservable) = when (timeline) {
                is ObservableT -> {
                    val timelineView = ObservableTimelineView(view.context)
                    timelineView.timeline = timeline
                    timelineView.readOnly = false
                    timelineView to timelineView.timelineObservable.cast(Timeline::class.java)
                }
                is SingleT -> {
                    val timelineView = SingleTimelineView(view.context)
                    timelineView.timeline = timeline
                    timelineView.readOnly = false
                    timelineView to timelineView.timelineObservable.cast(Timeline::class.java)
                }
                is MaybeT -> {
                    val timelineView = MaybeTimelineView(view.context)
                    timelineView.timeline = timeline
                    timelineView.readOnly = false
                    timelineView to timelineView.timelineObservable.cast(Timeline::class.java)
                }
                is CompletableT -> {
                    val timelineView = CompletableTimelineView(view.context)
                    timelineView.timeline = timeline
                    timelineView.readOnly = false
                    timelineView to timelineView.timelineObservable.cast(Timeline::class.java)
                }
            }
            timelineView.id = View.generateViewId()

            rootContainer.addView(
                    timelineView,
                    ViewGroup.LayoutParams(
                            ConstraintSet.MATCH_CONSTRAINT,
                            ConstraintSet.WRAP_CONTENT
                    )
            )

            ConstraintSet().apply {
                clone(rootContainer)

                connect(timelineView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                connect(timelineView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                connect(timelineView.id, ConstraintSet.BOTTOM, R.id.details_operator, ConstraintSet.TOP)

                applyTo(rootContainer)
            }
        }
    }
}
