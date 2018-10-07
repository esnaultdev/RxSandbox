package aodev.blue.rxsandbox.ui.screen.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.CompletableT
import aodev.blue.rxsandbox.model.MaybeT
import aodev.blue.rxsandbox.model.ObservableT
import aodev.blue.rxsandbox.model.SingleT
import aodev.blue.rxsandbox.model.TimelineType
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.sample.OperatorSample
import aodev.blue.rxsandbox.model.sample.getCompletableSample
import aodev.blue.rxsandbox.model.sample.getMaybeSample
import aodev.blue.rxsandbox.model.sample.getObservableSample
import aodev.blue.rxsandbox.model.sample.getSingleSample
import aodev.blue.rxsandbox.ui.screen.NavigationLabelListener
import aodev.blue.rxsandbox.ui.widget.operator.OperatorView
import aodev.blue.rxsandbox.ui.widget.timeline.CompletableTimelineView
import aodev.blue.rxsandbox.ui.widget.timeline.MaybeTimelineView
import aodev.blue.rxsandbox.ui.widget.timeline.ObservableTimelineView
import aodev.blue.rxsandbox.ui.widget.timeline.SingleTimelineView
import aodev.blue.rxsandbox.utils.openInBrowser
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
    private val timelineType: TimelineType by lazy(LazyThreadSafetyMode.NONE) {
        val streamTypeIndex = arguments?.getInt("timeline_type", 0) ?: 0
        TimelineType.values()[streamTypeIndex]
    }

    // UI
    private lateinit var rootContainer: ConstraintLayout
    private lateinit var constraintSet: ConstraintSet
    private var resultView: View? = null
    private var operatorView: OperatorView? = null

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

        val sample = when(timelineType) {
            TimelineType.OBSERVABLE -> getObservableSample(operatorName)
            TimelineType.SINGLE -> getSingleSample(operatorName)
            TimelineType.MAYBE -> getMaybeSample(operatorName)
            TimelineType.COMPLETABLE -> getCompletableSample(operatorName)
        }
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
        rootContainer = view.findViewById(R.id.details_content)
        constraintSet = ConstraintSet()
        constraintSet.clone(rootContainer)

        val operatorView = OperatorView(requireContext()).apply {
            text = sample.operator.expression
            id = View.generateViewId()
        }
        this.operatorView = operatorView

        rootContainer.addView(operatorView)

        constraintSet.run {
            constrainWidth(operatorView.id, ConstraintSet.MATCH_CONSTRAINT)
            constrainHeight(operatorView.id, ConstraintSet.WRAP_CONTENT)
            connect(operatorView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(operatorView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        }

        if (sample.input.isNotEmpty()) {
            // Currently just display the first timeline for simplicity
            val timeline: Timeline<Int> = sample.input.first()

            val (timelineView, timelineObservable) = createViewForTimeline(view.context, timeline, false)
            timelineView.id = View.generateViewId()

            rootContainer.addView(timelineView)

            constraintSet.run {
                constrainWidth(timelineView.id, ConstraintSet.MATCH_CONSTRAINT)
                constrainHeight(timelineView.id, ConstraintSet.WRAP_CONTENT)
                connect(timelineView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                connect(timelineView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                connect(operatorView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                connect(operatorView.id, ConstraintSet.TOP, timelineView.id, ConstraintSet.BOTTOM)
            }

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
            constraintSet.connect(operatorView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)

            Single.fromCallable { requireNotNull(sample.operator.apply(emptyList())) } // TODO Handle this gracefully
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onSuccess = { handleResultTimelineChanged(it) },
                            onError = { TODO("Handle an operator error") }
                    )
                    .addTo(disposables)
        }

        val documentationUrl = sample.operator.docUrl
        if (documentationUrl != null) {
            constraintSet.setVisibility(R.id.details_see_documentation, View.VISIBLE)
            view.findViewById<Button>(R.id.details_see_documentation).setOnClickListener {
                requireActivity().openInBrowser(documentationUrl)
            }
        }

        constraintSet.applyTo(rootContainer)
    }

    private fun handleResultTimelineChanged(timeline: Timeline<Int>) {
        val localResultView = resultView
        if (localResultView == null) {
            val (timelineView, _) = createViewForTimeline(requireContext(), timeline, true)
            timelineView.id = View.generateViewId()
            resultView = timelineView

            rootContainer.addView(timelineView)

            constraintSet.apply {
                constrainWidth(timelineView.id, ConstraintSet.MATCH_CONSTRAINT)
                constrainHeight(timelineView.id, ConstraintSet.WRAP_CONTENT)
                connect(timelineView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                connect(timelineView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                val topConstraintId = operatorView?.id ?: ConstraintSet.PARENT_ID
                connect(timelineView.id, ConstraintSet.TOP, topConstraintId, ConstraintSet.BOTTOM)

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
