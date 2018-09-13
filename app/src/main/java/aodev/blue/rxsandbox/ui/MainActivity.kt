package aodev.blue.rxsandbox.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.Event
import aodev.blue.rxsandbox.model.Termination
import aodev.blue.rxsandbox.model.TerminationEvent
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.operator.filtering.SkipOperator
import aodev.blue.rxsandbox.ui.widget.TimelineView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private var disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sourceTimelineView: TimelineView = findViewById(R.id.source_timeline)
        val operatorView: TextView = findViewById(R.id.operator)
        val resultTimelineView: TimelineView = findViewById(R.id.result_timeline)

        val operator = SkipOperator<Int>(2)
        val sourceTimeline = Timeline(
                listOf(
                        Event(0f, 0),
                        Event(2f, 1),
                        Event(4f, 2),
                        Event(6f, 3),
                        Event(8f, 4),
                        Event(10f, 5)
                ),
                TerminationEvent(Config.timelineDuration.toFloat(), Termination.Complete)
        )

        sourceTimelineView.timeline = sourceTimeline
        operatorView.text = operator.expression()
        resultTimelineView.readOnly = true

        sourceTimelineView.timelineFlowable
                .subscribeOn(Schedulers.computation())
                .map { operator.apply(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy { resultTimelineView.timeline = it }
                .addTo(disposables)
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }
}
