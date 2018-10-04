package aodev.blue.rxsandbox.ui.screen.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.cast
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers


class DetailsFragment : Fragment() {

    // Arguments
    private val operatorName: String by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getString("operator_name") ?: ""
    }

    // UI
    private var resultView: View? = null
    private lateinit var rootContainer: ConstraintLayout
    private lateinit var constraintSet: ConstraintSet

    // Rx
    private val disposables = CompositeDisposable()

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

    override fun onDestroyView() {
        super.onDestroyView()

        disposables.clear()
    }

    private fun setupViews(view: View, sample: OperatorSample) {
        val operatorView: OperatorView = view.findViewById(R.id.details_operator)
        operatorView.text = sample.operator.expression()

        rootContainer = view.findViewById(R.id.details_root)
        constraintSet = ConstraintSet()

        if (sample.input.isNotEmpty()) {
            // Currently just display the first timeline for simplicity
            val timeline: Timeline<Int> = sample.input.first()

            val (timelineView, timelineObservable) = createViewForTimeline(view.context, timeline, false)
            timelineView.id = View.generateViewId()

            rootContainer.addView(
                    timelineView,
                    ViewGroup.LayoutParams(
                            ConstraintSet.MATCH_CONSTRAINT,
                            ConstraintSet.WRAP_CONTENT
                    )
            )
            constraintSet.clone(rootContainer)

            constraintSet.run {
                connect(timelineView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                connect(timelineView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                connect(timelineView.id, ConstraintSet.BOTTOM, R.id.details_operator, ConstraintSet.TOP)
            }
            constraintSet.applyTo(rootContainer)

            val inputObservables = listOf(timelineObservable)

            val inputFlowables = inputObservables.map { it.toFlowable(BackpressureStrategy.LATEST) }
            Flowable.combineLatest(inputFlowables) { inputsArray ->
                @Suppress("UNCHECKED_CAST")
                inputsArray.map { it as Timeline<Int> }
            }
                    .map { requireNotNull(sample.operator.apply(it)) } // TODO Handle this gracefully
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onNext = { handleResultTimelineChanged(it) },
                            onError = { TODO("Handle an operator error") }
                    )
                    .addTo(disposables)
        } else {
            Single.fromCallable { requireNotNull(sample.operator.apply(emptyList())) } // TODO Handle this gracefully
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onSuccess = { handleResultTimelineChanged(it) },
                            onError = { TODO("Handle an operator error") }
                    )
                    .addTo(disposables)
        }
    }

    private fun handleResultTimelineChanged(timeline: Timeline<Int>) {
        val localResultView = resultView
        if (localResultView == null) {
            val (timelineView, _) = createViewForTimeline(requireContext(), timeline, true)
            timelineView.id = View.generateViewId()
            resultView = timelineView

            rootContainer.addView(
                    timelineView,
                    ViewGroup.LayoutParams(
                            ConstraintSet.MATCH_CONSTRAINT,
                            ConstraintSet.WRAP_CONTENT
                    )
            )
            constraintSet.clone(rootContainer)

            constraintSet.apply {
                connect(timelineView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                connect(timelineView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                connect(timelineView.id, ConstraintSet.TOP, R.id.details_operator, ConstraintSet.BOTTOM)

                applyTo(rootContainer)
            }
        } else {
            when (timeline) {
                is ObservableT -> (localResultView as? ObservableTimelineView)?.timeline = timeline
                is SingleT -> (localResultView as? SingleTimelineView)?.timeline = timeline
                is MaybeT -> (localResultView as? MaybeTimelineView)?.timeline = timeline
                is CompletableT -> (localResultView as? CompletableTimelineView)?.timeline = timeline
            }
        }
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
