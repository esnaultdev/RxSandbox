package aodev.blue.rxsandbox.ui.widget.timeline

import android.view.MotionEvent
import aodev.blue.rxsandbox.model.Config
import aodev.blue.rxsandbox.utils.clamp


class GestureHelper(
        private val timePositionMapper: TimePositionMapper,
        private val isTouchingResult: (x: Float, y: Float) -> Boolean,
        private val onMoved: (newTime: Float) -> Unit
) {

    private var activePointerId = MotionEvent.INVALID_POINTER_ID
    private var isMoving = false

    fun onTouchEvent(ev: MotionEvent) {
        val action = ev.actionMasked

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val pointerIndex = ev.actionIndex
                val x = ev.getX(pointerIndex)
                val y = ev.getY(pointerIndex)

                if (isTouchingResult(x, y)) {
                    isMoving = true
                    activePointerId = ev.getPointerId(0)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isMoving) {
                    val pointerIndex = ev.findPointerIndex(activePointerId)

                    val x = ev.getX(pointerIndex)

                    val newTime = timePositionMapper.time(x).clamp(0f, Config.timelineDuration.toFloat())

                    if (isMoving) {
                        onMoved(newTime)
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                activePointerId = MotionEvent.INVALID_POINTER_ID
                isMoving = false
            }

            MotionEvent.ACTION_CANCEL -> {
                activePointerId = MotionEvent.INVALID_POINTER_ID
                isMoving = false
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = ev.actionIndex
                val pointerId = ev.getPointerId(pointerIndex)

                if (pointerId == activePointerId) {
                    // This was our active pointer going up. Choose a new active pointer.
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0

                    activePointerId = ev.getPointerId(newPointerIndex)
                }
            }
        }
    }

    fun resetCurrentGesture() {
        activePointerId = MotionEvent.INVALID_POINTER_ID
        isMoving = false
    }
}