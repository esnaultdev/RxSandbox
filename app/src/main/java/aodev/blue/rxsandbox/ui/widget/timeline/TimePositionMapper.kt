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
            timeFactor * availableWidth + totalPaddingStart
        } else {
            (1 - timeFactor) * availableWidth + totalPaddingEnd
        }
    }

    fun time(position: Float): Float {
        return if (isLtr) {
            val factor = (position - totalPaddingStart) / availableWidth
            factor * Config.timelineDuration
        } else {
            val factor = (position - totalPaddingEnd) / availableWidth
            (1 - factor) * Config.timelineDuration
        }
    }
}
