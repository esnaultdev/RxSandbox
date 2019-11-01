package aodev.blue.rxsandbox.ui.widget.timeline.drawer

import android.content.Context
import android.graphics.Canvas
import aodev.blue.rxsandbox.model.*
import aodev.blue.rxsandbox.ui.widget.timeline.TimePositionMapper
import aodev.blue.rxsandbox.utils.exhaustive


/**
 * Draw the events of a timeline.
 */
class EventDrawer(
        private val context: Context,
        private val timePositionMapper: TimePositionMapper
) {

    /* *****************************************************************************************************************/
    //region Exposed methods/values **************************************************************************/

    /**
     * The height of the timeline line. Events will be drawn centered around this value.
     */
    var centerHeight: Float = 0f

    fun draw(canvas: Canvas, timeline: Timeline<Any>?) {
        when (timeline) {
            null -> Unit
            is ObservableT -> drawObservable(canvas, timeline)
            is SingleT -> drawSingle(canvas, timeline)
            is MaybeT -> drawMaybe(canvas, timeline)
            is CompletableT -> drawCompletable(canvas, timeline)
        }.exhaustive
    }

    //endregion

    /* *****************************************************************************************************************/
    //region Sub drawers **************************************************************************/

    private val valueEventDrawer: ValueEventDrawer by lazy(LazyThreadSafetyMode.NONE) {
        ValueEventDrawer(context)
    }

    private val completeEventDrawer: CompleteEventDrawer by lazy(LazyThreadSafetyMode.NONE) {
        CompleteEventDrawer(context)
    }

    private val errorEventDrawer: ErrorEventDrawer by lazy(LazyThreadSafetyMode.NONE) {
        ErrorEventDrawer(context)
    }

    //endregion

    /* *****************************************************************************************************************/
    //region Observable **************************************************************************/

    private fun drawObservable(canvas: Canvas, timeline: ObservableT<Any>) {
        drawTerminationEvent(canvas, timeline)
        drawEvents(canvas, timeline.events)
    }

    private fun drawTerminationEvent(canvas: Canvas, timeline: ObservableT<Any>) {
        when (val termination = timeline.termination) {
            is ObservableT.Termination.None -> Unit
            else -> {
                // Get the position of the last value event
                val previousPosition = timeline.events.lastOrNull()
                        ?.time
                        ?.let(timePositionMapper::position)

                when (termination) {
                    is ObservableT.Termination.Complete -> {
                        val position = timePositionMapper.position(termination.time)
                        completeEventDrawer.draw(canvas, position, centerHeight, previousPosition)
                    }
                    is ObservableT.Termination.Error -> {
                        val position = timePositionMapper.position(termination.time)
                        errorEventDrawer.draw(canvas, position, centerHeight, previousPosition)
                    }
                    else -> Unit // Already handled, but the compiler still wants it
                }
            }
        }.exhaustive
    }

    private fun drawEvents(canvas: Canvas, sortedEvents: List<ObservableT.Event<Any>>) {
        sortedEvents.reversed().forEach { event ->
            val position = timePositionMapper.position(event.time)
            valueEventDrawer.draw(canvas, position, centerHeight, event.value)
        }
    }

    //endregion

    /* *****************************************************************************************************************/
    //region Single **************************************************************************/

    private fun drawSingle(canvas: Canvas, timeline: SingleT<Any>) {
        when (val result = timeline.result) {
            is SingleT.Result.None -> Unit
            is SingleT.Result.Success -> {
                val position = timePositionMapper.position(result.time)
                valueEventDrawer.draw(canvas, position, centerHeight, result.value)
            }
            is SingleT.Result.Error -> {
                val position = timePositionMapper.position(result.time)
                errorEventDrawer.draw(canvas, position, centerHeight, null)
            }
        }.exhaustive
    }

    //endregion

    /* *****************************************************************************************************************/
    //region Maybe **************************************************************************/

    private fun drawMaybe(canvas: Canvas, timeline: MaybeT<Any>) {
        when (val result = timeline.result) {
            is MaybeT.Result.None -> Unit
            is MaybeT.Result.Success -> {
                val position = timePositionMapper.position(result.time)
                valueEventDrawer.draw(canvas, position, centerHeight, result.value)
            }
            is MaybeT.Result.Complete -> {
                val position = timePositionMapper.position(result.time)
                completeEventDrawer.draw(canvas, position, centerHeight, null)
            }
            is MaybeT.Result.Error -> {
                val position = timePositionMapper.position(result.time)
                errorEventDrawer.draw(canvas, position, centerHeight, null)
            }
        }.exhaustive
    }

    //endregion

    /* *****************************************************************************************************************/
    //region Completable **************************************************************************/

    private fun drawCompletable(canvas: Canvas, timeline: CompletableT) {
        when (val result = timeline.result) {
            is CompletableT.Result.None -> Unit
            is CompletableT.Result.Complete -> {
                val position = timePositionMapper.position(result.time)
                completeEventDrawer.draw(canvas, position, centerHeight, null)
            }
            is CompletableT.Result.Error -> {
                val position = timePositionMapper.position(result.time)
                errorEventDrawer.draw(canvas, position, centerHeight, null)
            }
        }.exhaustive
    }

    //endregion
}
