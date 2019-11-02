package aodev.blue.rxsandbox.ui.widget.model

sealed class TimelineSelection {
    object None : TimelineSelection()
    object Alone : TimelineSelection()
    data class Checkbox(
            val selected: Boolean,
            val connected: Boolean
    ) : TimelineSelection()
}
