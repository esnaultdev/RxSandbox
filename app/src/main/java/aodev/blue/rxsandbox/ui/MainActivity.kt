package aodev.blue.rxsandbox.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.observable.ObservableEvent
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline
import aodev.blue.rxsandbox.model.operations.predicate.EvenPredicate
import aodev.blue.rxsandbox.model.observable.operators.filter.ObservableFilter
import aodev.blue.rxsandbox.ui.widget.ObservableTimelineView
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

        val sourceTimelineView: ObservableTimelineView = findViewById(R.id.source_timeline)
        val operatorView: TextView = findViewById(R.id.operator)
        val resultTimelineView: ObservableTimelineView = findViewById(R.id.result_timeline)

        val operator = ObservableFilter(EvenPredicate())
        val sourceTimeline = ObservableTimeline(
                setOf(
                        ObservableEvent(0f, 0),
                        ObservableEvent(2f, 1),
                        ObservableEvent(4f, 2),
                        ObservableEvent(6f, 3),
                        ObservableEvent(8f, 4),
                        ObservableEvent(10f, 5)
                ),
                ObservableTermination.Complete(Config.timelineDuration.toFloat())
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
