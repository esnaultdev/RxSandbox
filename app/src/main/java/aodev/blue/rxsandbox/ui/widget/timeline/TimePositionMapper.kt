package aodev.blue.rxsandbox.ui.widget.timeline

import aodev.blue.rxsandbox.model.Config


class TimePositionMapper(
        private val padding: Float,
        private val innerPaddingStart: Float,
        private val innerPaddingEnd: Float
) {

    var isLtr: Boolean = true
    var width: Int = 0

    val availableWidth: Float
        get() = width - 2 * padding - innerPaddingStart - innerPaddingEnd

    fun position(time: Float): Float {
        val timeFactor = time / Config.timelineDuration
        return if (isLtr) {
            timeFactor * availableWidth + padding + innerPaddingStart
        } else {
            (1 - timeFactor) * availableWidth + padding + innerPaddingEnd
        }
    }

    fun time(position: Float): Float {
        return if (isLtr) {
            (position - padding - innerPaddingStart) / availableWidth * Config.timelineDuration
        } else {
            (1 - ((position - padding - innerPaddingEnd) / availableWidth)) * Config.timelineDuration
        }
    }
}