package aodev.blue.rxsandbox.ui.widget.treeview

import aodev.blue.rxsandbox.ui.widget.treeview.model.TreeViewModel as ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * Update a [ViewModel] by computing its results asynchronously.
 */
class TreeUpdater {

    var viewModel: ViewModel? = null
        set(value) {
            field = value
            cancel()
            updateTimelines()
        }

    private var job: Job? = null

    fun updateTimelines() {
        job?.cancel()

        val viewModel = viewModel ?: return
        job = GlobalScope.launch {
            updateTimelinesAsync(viewModel)
        }
    }

    private fun CoroutineScope.updateTimelinesAsync(viewModel: ViewModel) {
        viewModel.elements.forEach { element ->
            if (element is ViewModel.Element.TimelineE && !element.shown.get()) {
                val result = element.result()
                launch(Dispatchers.Main) {
                    element.update(result)
                    element.shown.set(true)
                }
            }
        }
    }

    private fun cancel() {
        job?.cancel()
        job = null
    }
}
