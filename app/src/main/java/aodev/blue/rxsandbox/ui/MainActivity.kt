package aodev.blue.rxsandbox.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.Event
import aodev.blue.rxsandbox.model.Termination
import aodev.blue.rxsandbox.model.TerminationEvent
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.model.mapping.MultiplyMapping
import aodev.blue.rxsandbox.model.operator.transform.MapOperator
import aodev.blue.rxsandbox.ui.widget.TimelineView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timelineVew: TimelineView = findViewById(R.id.timeline)

        val operator = MapOperator(MultiplyMapping())
        val timeline = Timeline(
                listOf(
                        Event(2f, 1),
                        Event(4f, 2),
                        Event(6f, 3),
                        Event(8f, 4)
                ),
                TerminationEvent(Config.timelineDuration.toFloat(), Termination.Complete)
        )
        val mappedTimeline = operator.apply(timeline)

        timelineVew.timeline = mappedTimeline
    }
}
