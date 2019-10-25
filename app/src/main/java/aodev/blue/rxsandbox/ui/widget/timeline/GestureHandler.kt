package aodev.blue.rxsandbox.ui.widget.timeline

import android.content.Context
import android.view.MotionEvent
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.*
import aodev.blue.rxsandbox.utils.clamp
import aodev.blue.rxsandbox.utils.toLinkedList
import kotlin.math.abs
import kotlin.reflect.KMutableProperty0


class GestureHandler(
        context: Context,
        private val timePositionMapper: TimePositionMapper,
        private val timelineProp: KMutableProperty0<Timeline<Any>?>
) {

    object EventIndex {
        const val NONE = -2
        const val TERMINATION = -1
    }

    private val touchTargetSize = context.resources.getDimension(R.dimen.timeline_touch_target_size)

    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var movingEventIndex = EventIndex.NONE
    private val isMoving: Boolean
        get() = movingEventIndex != EventIndex.NONE
    /**
     * A mutable list of the observable events, kept up-to-date while
     */
    private var observableEvents: MutableList<ObservableT.Event<Any>>? = null

    /* *****************************************************************************************************************/
    //region Exposed methods/values **************************************************************************/

    /**
     * The height of the timeline line. Gesture too far from this are ignored.
     */
    var centerHeight: Float = 0f

    fun onTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked

        return when (action) {
            MotionEvent.ACTION_DOWN -> handleActionDown(ev)
            MotionEvent.ACTION_MOVE -> handleActionMove(ev)
            MotionEvent.ACTION_POINTER_UP -> handleActionPointerUp(ev)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                cancelGesture()
                true
            }
            else -> false
        }
    }

    fun resetCurrentGesture() {
        cancelGesture()
    }

    //endregion

    /* *****************************************************************************************************************/
    //region Touch events **************************************************************************/

    private fun cancelGesture() {
        activePointerId = MotionEvent.INVALID_POINTER_ID
        movingEventIndex = EventIndex.NONE
        observableEvents = null
    }

    private fun handleActionDown(ev: MotionEvent): Boolean {
        val pointerIndex = ev.actionIndex
        val x = ev.getX(pointerIndex)
        val y = ev.getY(pointerIndex)

        val eventIndex = getEventIndex(x, y)
        if (eventIndex != EventIndex.NONE) {
            activePointerId = ev.getPointerId(0)
            movingEventIndex = eventIndex

            val timeline = timelineProp.get()
            if (timeline is ObservableT) {
                observableEvents = timeline.events.toLinkedList()
            }
        }

        return true
    }

    private fun handleActionMove(ev: MotionEvent): Boolean {
        if (isMoving) {
            val pointerIndex = ev.findPointerIndex(activePointerId)

            val x = ev.getX(pointerIndex)

            val newTime = timePositionMapper.time(x).clamp(0f, Config.timelineDuration.toFloat())

            handleEventMove(newTime)
        }

        return true
    }

    private fun handleActionPointerUp(ev: MotionEvent): Boolean {
        val pointerIndex = ev.actionIndex
        val pointerId = ev.getPointerId(pointerIndex)

        if (pointerId == activePointerId) {
            // This was our active pointer going up. Choose a new active pointer.
            val newPointerIndex = if (pointerIndex == 0) 1 else 0

            activePointerId = ev.getPointerId(newPointerIndex)
        }

        return true
    }

    //endregion

    /* *****************************************************************************************************************/
    //region Event index **************************************************************************/

    private fun getEventIndex(x: Float, y: Float): Int {
        if (!isHeightInTouchTarget(y)) return EventIndex.NONE

        return when (val timeline = timelineProp.get()) {
            null -> EventIndex.NONE
            is ObservableT -> getEventIndexObservable(timeline, x)
            is SingleT -> getEventIndexSingle(timeline, x)
            is MaybeT -> getEventIndexMaybe(timeline, x)
            is CompletableT -> getEventIndexCompletable(timeline, x)
        }
    }

    private fun isHeightInTouchTarget(y: Float): Boolean {
        val halfTargetSize = touchTargetSize / 2
        return y >= centerHeight - halfTargetSize && y <= centerHeight + halfTargetSize
    }

    private fun getEventIndexObservable(timeline: ObservableT<Any>, x: Float): Int {
        val eventTimes = timeline.events.map { it.time }
        val terminationTime = timeline.termination.time

        val allEventTimes = if (terminationTime != null) {
            eventTimes + terminationTime
        } else {
            eventTimes
        }

        return allEventTimes.withIndex()
                .filter { isTouchingEventWithTime(x, it.value) }
                // TODO Improve this minBy since the times are sorted
                .minBy { abs(it.value - x) }
                ?.index
                ?.let { if (it == timeline.events.size) EventIndex.TERMINATION else it }
                ?: EventIndex.NONE
    }

    private fun getEventIndexSingle(timeline: SingleT<Any>, x: Float): Int {
        val isTouchingResult = timeline.result.time
                ?.let { isTouchingEventWithTime(x, it) }
                ?: false

        return if (isTouchingResult) EventIndex.TERMINATION else EventIndex.NONE
    }

    private fun getEventIndexMaybe(timeline: MaybeT<Any>, x: Float): Int {
        val isTouchingResult = timeline.result.time
                ?.let { isTouchingEventWithTime(x, it) }
                ?: false

        return if (isTouchingResult) EventIndex.TERMINATION else EventIndex.NONE
    }

    private fun getEventIndexCompletable(timeline: CompletableT, x: Float): Int {
        val isTouchingResult = timeline.result.time
                ?.let { isTouchingEventWithTime(x, it) }
                ?: false

        return if (isTouchingResult) EventIndex.TERMINATION else EventIndex.NONE
    }

    private fun isTouchingEventWithTime(x: Float, eventTime: Float): Boolean {
        val eventPosition = timePositionMapper.position(eventTime)
        val halfTargetSize = touchTargetSize / 2
        return x >= eventPosition - halfTargetSize && x <= eventPosition + halfTargetSize
    }

    //endregion

    /* *****************************************************************************************************************/
    //region Event move **************************************************************************/

    private fun handleEventMove(newTime: Float) {
        when (val timeline = timelineProp.get()) {
            null -> Unit
            is ObservableT -> handleEventMoveObservable(timeline, newTime, movingEventIndex)
            is SingleT -> handleEventMoveSingle(timeline, newTime)
            is MaybeT -> handleEventMoveMaybe(timeline, newTime)
            is CompletableT -> handleEventMoveCompletable(timeline, newTime)
        }
    }

    private fun handleEventMoveSingle(timeline: SingleT<Any>, newTime: Float) {
        val newResult = when (val result = timeline.result) {
            is SingleT.Result.None -> return
            is SingleT.Result.Success -> SingleT.Result.Success(newTime, result.value)
            is SingleT.Result.Error -> SingleT.Result.Error<Any>(newTime)
        }
        val newTimeline = timeline.copy(result = newResult)
        timelineProp.set(newTimeline)
    }

    private fun handleEventMoveMaybe(timeline: MaybeT<Any>, newTime: Float) {
        val newResult = when (val result = timeline.result) {
            is MaybeT.Result.None -> return
            is MaybeT.Result.Complete -> MaybeT.Result.Complete(newTime)
            is MaybeT.Result.Success -> MaybeT.Result.Success(newTime, result.value)
            is MaybeT.Result.Error -> MaybeT.Result.Error<Any>(newTime)
        }
        val newTimeline = timeline.copy(result = newResult)
        timelineProp.set(newTimeline)
    }

    private fun handleEventMoveCompletable(timeline: CompletableT, newTime: Float) {
        val newResult = when (timeline.result) {
            is CompletableT.Result.None -> return
            is CompletableT.Result.Complete -> CompletableT.Result.Complete(newTime)
            is CompletableT.Result.Error -> CompletableT.Result.Error(newTime)
        }
        val newTimeline = timeline.copy(result = newResult)
        timelineProp.set(newTimeline)
    }

    //endregion

    /* *****************************************************************************************************************/
    //region Event move (Observable) **************************************************************************/

    private fun handleEventMoveObservable(
            timeline: ObservableT<Any>,
            newTime: Float,
            movingIndex: Int) {
        when (movingIndex) {
            EventIndex.NONE -> Unit
            EventIndex.TERMINATION -> moveObservableTermination(timeline, newTime)
            else -> moveObservableEvent(timeline, newTime, movingIndex)
        }
    }

    private fun moveObservableTermination(timeline: ObservableT<Any>, newTime: Float) {
        val newTermination = when (timeline.termination) {
            ObservableT.Termination.None -> return
            is ObservableT.Termination.Complete -> ObservableT.Termination.Complete(newTime)
            is ObservableT.Termination.Error -> ObservableT.Termination.Error(newTime)
        }

        // Move the events that might be after the termination
        // Observable events should have already been setup
        val events = observableEvents ?: return
        val firstToMoveIndex = events.indexOfFirst { it.time > newTime }
        if (firstToMoveIndex != -1) {
            for (i in firstToMoveIndex until events.size) {
                events[i] = events[i].moveTo(newTime)
            }
        }

        val newTimeline = timeline.copy(events = events, termination = newTermination)
        timelineProp.set(newTimeline)
    }

    private fun moveObservableEvent(timeline: ObservableT<Any>, newTime: Float, movingIndex: Int) {
        // Observable events should have already been setup
        val events = observableEvents ?: return
        if (events.size <= movingIndex) return

        val oldEvent = events[movingIndex]
        val oldTime = oldEvent.time

        // If the time hasn't changed, we don't need to update the timeline
        // While this is very unlikely, we can return early in this case
        if (oldTime == newTime) return

        val newEvent = oldEvent.moveTo(newTime)

        // Move the event to its new index to preserve the sort by time
        if (events.size != 1) {
            if (newTime < oldTime) {
                var index = movingIndex - 1
                while (index >= 0 && events[index].time > newTime) {
                    index--
                }
                ++index // The check is done one spot too early, our index is right after that
                if (index != movingIndex) {
                    events.removeAt(movingIndex)
                    events.add(index, newEvent)
                    // TODO Move this side effect somewhere testable
                    movingEventIndex = movingIndex - 1
                } else {
                    events[movingIndex] = newEvent
                }
            } else if (newTime > oldTime) {
                var index = movingIndex + 1
                while (index < events.size && events[index].time < newTime) {
                    index++
                }
                index-- // The check is done one spot too far, our index is right before that
                if (index != movingIndex) {
                    events.removeAt(movingIndex)
                    events.add(index, newEvent)
                    // TODO Move this side effect somewhere testable
                    movingEventIndex = movingIndex + 1
                } else {
                    events[movingIndex] = newEvent
                }
            } // else the time is the same as before, which was already accounted for
        } else {
            events[movingIndex] = newEvent
        }

        // Move the termination if needed
        val newTermination = when (val termination = timeline.termination) {
            ObservableT.Termination.None -> termination
            is ObservableT.Termination.Complete -> {
                if (termination.time < newTime) {
                    ObservableT.Termination.Complete(newTime)
                } else {
                    termination
                }
            }
            is ObservableT.Termination.Error -> {
                if (termination.time < newTime) {
                    ObservableT.Termination.Error(newTime)
                } else {
                    termination
                }
            }
        }

        val newTimeline = timeline.copy(events = events, termination = newTermination)
        timelineProp.set(newTimeline)
    }

    //endregion
}
