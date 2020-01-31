package aodev.blue.rxsandbox.ui.widget.treeview

import aodev.blue.rxsandbox.ui.widget.treeview.model.buildViewModel
import aodev.blue.rxsandbox.ui.widget.treeview.model.TreeViewState as ViewState
import aodev.blue.rxsandbox.ui.widget.treeview.model.TreeViewModel as ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * Build a [ViewModel] from a [ViewState] and compute its timelines asynchronously.
 */
class TreeUpdater(private val updateViews: (ViewModel) -> Unit) {

    var viewState: ViewState? = null
        set(value) {
            field = value
            updateViewModel()
        }

    var viewModel: ViewModel? = null
        set(value) {
            field = value
            updateTimelines()
        }

    private var job: Job? = null

    fun updateViewModel() {
        // Since we always rebuild the view model and the associated views, we need to clear the
        // shown flag to show the current state
        // TODO Improve all of this by preserving the UI when possible
        val viewState = viewState ?: return
        resetShownFlag(viewState.bottomElement)

        viewModel = buildViewModel(viewState, this)
        viewModel?.let(updateViews)
    }

    private fun resetShownFlag(stateElement: ViewState.Element<*, *>) {
        stateElement.shown = false
        stateElement.previous.forEach { previous -> previous?.let(this::resetShownFlag) }
    }

    fun updateTimelines() {
        cancel()

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
