package aodev.blue.rxsandbox.ui.widget.timeline

import aodev.blue.rxsandbox.model.Config


class TimePositionMapper(
        private val totalPaddingStart: Float,
        private val totalPaddingEnd: Float
) {

    var isLtr: Boolean = true
    var width: Int = 0

    private val availableWidth: Float
        get() = width - totalPaddingStart - totalPaddingEnd

    fun position(time: Float): Float {
        val timeFactor = time / Config.timelineDuration
        return if (isLtr) {
            timeFactor * availableWidth + totalPaddingEnd
        } else {
            (1 - timeFactor) * availableWidth + totalPaddingEnd
        }
    }

    fun time(position: Float): Float {
        val factor = (position - totalPaddingStart) / availableWidth
        return if (isLtr) {
            factor * Config.timelineDuration
        } else {
            (1 - factor) * Config.timelineDuration
        }
    }
}
