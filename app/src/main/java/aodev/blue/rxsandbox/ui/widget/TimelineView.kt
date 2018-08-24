package aodev.blue.rxsandbox.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.model.Event
import aodev.blue.rxsandbox.model.Termination
import aodev.blue.rxsandbox.model.TerminationEvent
import aodev.blue.rxsandbox.model.Timeline
import aodev.blue.rxsandbox.ui.utils.extension.colorCompat
import kotlin.properties.Delegates


// TODO Add RTL support
// TODO Add inner horizontal padding
// TODO Fix the text alignment
// TODO Fix the arrow not being displayed
class TimelineView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)


    init {
        if (isInEditMode) {
            timeline = Timeline(
                    listOf(
                            Event(2f, 1),
                            Event(4f, 2),
                            Event(6f, 3),
                            Event(8f, 4)
                    ),
                    TerminationEvent(Config.timelineDuration.toFloat(), Termination.Complete)
            )
        }
    }


    // Data
    var timeline by Delegates.observable<Timeline<Int>?>(null) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            invalidate()
        }
    }


    // Ressources
    private val strokeWidth = context.resources.getDimension(R.dimen.timeline_stroke_width)
    private val padding = context.resources.getDimension(R.dimen.timeline_padding)
    private val eventSize = context.resources.getDimension(R.dimen.timeline_event_size)
    private val eventTextSize = context.resources.getDimension(R.dimen.timeline_event_text_size)
    private val completeHeight = context.resources.getDimension(R.dimen.timeline_complete_height)
    private val errorSize = context.resources.getDimension(R.dimen.timeline_error_size)
    private val arrowWidth = context.resources.getDimension(R.dimen.timeline_arrow_width)
    private val arrowHeight = context.resources.getDimension(R.dimen.timeline_arrow_height)
    private val strokeColor = context.colorCompat(R.color.timeline_stroke_color)
    private val eventFillColor = context.colorCompat(R.color.timeline_event_fill_color)
    private val eventTextColor = context.colorCompat(R.color.timeline_event_text_color)
    private val errorColor = context.colorCompat(R.color.timeline_error_color)


    // Paint
    private val strokePaint = Paint().apply {
        color = strokeColor
        strokeWidth = this@TimelineView.strokeWidth
        style = Paint.Style.STROKE
    }
    private val arrowPaint = Paint().apply {
        color = strokeColor
        style = Paint.Style.FILL
    }
    private val eventFillPaint = Paint().apply {
        color = eventFillColor
        style = Paint.Style.FILL
    }
    private val eventTextPaint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        color = eventTextColor
        textSize = eventTextSize
    }
    private val errorPaint = Paint().apply {
        color = errorColor
    }

    // Path
    private val arrowPath = Path().apply {
        // Starting from the pointy end
        rMoveTo(-arrowWidth, arrowHeight / 2)
        rMoveTo(0f, arrowHeight)
        close()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = (2 * padding + 10 * eventSize).toInt()
        val desiredHeight = (2 * padding + eventSize).toInt()

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            View.MeasureSpec.EXACTLY -> widthSize
            View.MeasureSpec.AT_MOST -> minOf(desiredWidth, widthSize)
            View.MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> throw IllegalArgumentException("Illegal measure spec")
        }

        val height = when (heightMode) {
            View.MeasureSpec.EXACTLY -> heightSize
            View.MeasureSpec.AT_MOST -> minOf(desiredHeight, heightSize)
            View.MeasureSpec.UNSPECIFIED -> desiredHeight
            else -> throw IllegalArgumentException("Illegal measure spec")
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawLine(canvas)
        timeline?.let { timeline ->
            drawTerminationEvent(canvas, timeline.termination)
            drawEvents(canvas, timeline.events)
        }
    }

    private fun drawLine(canvas: Canvas) {
        val centerHeight = height.toFloat() / 2

        canvas.drawLine(padding, centerHeight, width - padding, centerHeight, strokePaint)

        canvas.save()
        canvas.translate(width - padding, centerHeight)
        canvas.drawPath(arrowPath, arrowPaint)
        canvas.restore()
    }

    private fun drawTerminationEvent(canvas: Canvas, event: TerminationEvent) {
        val centerHeight = height.toFloat() / 2
        val position = eventPosition(event.time)

        when (event.value) {
            is Termination.Complete -> {
                canvas.drawLine(
                        position,
                        centerHeight - completeHeight / 2,
                        position,
                        centerHeight + completeHeight / 2,
                        strokePaint
                )
            }
            is Termination.Error -> {
                canvas.drawLine(
                        position - errorSize / 2,
                        position - errorSize / 2,
                        position + errorSize / 2,
                        position + errorSize / 2,
                        errorPaint
                )
                canvas.drawLine(
                        position - errorSize / 2,
                        position + errorSize / 2,
                        position + errorSize / 2,
                        position - errorSize / 2,
                        errorPaint
                )
            }
        }
    }

    private fun drawEvents(canvas: Canvas, events: List<Event<Int>>) {
        val centerHeight = height.toFloat() / 2
        events.forEach { event ->
            val position = eventPosition(event.time)

            canvas.drawCircle(position, centerHeight, eventSize / 2, eventFillPaint)
            canvas.drawCircle(position, centerHeight, eventSize / 2, strokePaint)
            canvas.drawText(event.value.toString(), position, centerHeight, eventTextPaint)
        }
    }

    private fun eventPosition(time: Float): Float {
        return time / Config.timelineDuration * (width - 2 * padding) + padding
    }
}