package aodev.blue.rxsandbox.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.completable.CompletableResult
import aodev.blue.rxsandbox.model.completable.CompletableTimeline
import aodev.blue.rxsandbox.model.completable.operators.utility.CompletableDelay
import aodev.blue.rxsandbox.model.observable.ObservableEvent
import aodev.blue.rxsandbox.model.observable.ObservableTermination
import aodev.blue.rxsandbox.model.observable.ObservableTimeline
import aodev.blue.rxsandbox.model.observable.operators.utility.ObservableDelay
import aodev.blue.rxsandbox.ui.widget.CompletableTimelineView
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

        val sourceTimelineView: CompletableTimelineView = findViewById(R.id.source_timeline)
        val operatorView: TextView = findViewById(R.id.operator)
        val resultTimelineView: CompletableTimelineView = findViewById(R.id.result_timeline)

        val operator = CompletableDelay(2f)
        val sourceTimeline = CompletableTimeline(CompletableResult.Complete(0f))

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
