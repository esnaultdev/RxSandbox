package aodev.blue.rxsandbox.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.maybe.MaybeResult
import aodev.blue.rxsandbox.model.maybe.MaybeTimeline
import aodev.blue.rxsandbox.model.maybe.operators.utility.MaybeDelay
import aodev.blue.rxsandbox.ui.widget.timeline.MaybeTimelineView
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

        val sourceTimelineView: MaybeTimelineView = findViewById(R.id.source_timeline)
        val operatorView: TextView = findViewById(R.id.operator)
        val resultTimelineView: MaybeTimelineView = findViewById(R.id.result_timeline)

        val operator = MaybeDelay<Int>(2f)
        val sourceTimeline = MaybeTimeline(MaybeResult.Success(0f, 0))

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
