package aodev.blue.rxsandbox.ui.screen.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.TimelineType
import aodev.blue.rxsandbox.model.completableOperators
import aodev.blue.rxsandbox.model.maybeOperators
import aodev.blue.rxsandbox.model.observableOperators
import aodev.blue.rxsandbox.model.singleOperators
import aodev.blue.rxsandbox.ui.screen.NavigationLabelListener


class ListFragment : Fragment() {

    private val navController
        get() = findNavController()

    private val timelineType: TimelineType by lazy(LazyThreadSafetyMode.NONE) {
        val streamTypeIndex = arguments?.getInt("timeline_type", 0) ?: 0
        TimelineType.values()[streamTypeIndex]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOperatorsList(view)
        setupScreenTitle()
    }

    private fun setupOperatorsList(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = OperatorAdapter(requireContext(), this::onOperatorClicked)
        recyclerView.adapter = adapter

        adapter.categories = when (timelineType) {
            TimelineType.OBSERVABLE -> observableOperators
            TimelineType.SINGLE -> singleOperators
            TimelineType.MAYBE -> maybeOperators
            TimelineType.COMPLETABLE -> completableOperators
        }
    }

    private fun setupScreenTitle() {
        val screenTitleResId = when (timelineType) {
            TimelineType.OBSERVABLE -> R.string.operators_list_screen_title_observable
            TimelineType.SINGLE -> R.string.operators_list_screen_title_single
            TimelineType.MAYBE -> R.string.operators_list_screen_title_maybe
            TimelineType.COMPLETABLE -> R.string.operators_list_screen_title_completable
        }
        (activity as? NavigationLabelListener)?.updateLabel(getString(screenTitleResId))
    }

    private fun onOperatorClicked(name: String) {
        val arguments = Bundle().apply {
            putString("operator_name", name)
            putInt("timeline_type", timelineType.ordinal)
        }
        navController.navigate(R.id.action_see_details, arguments)
    }
}