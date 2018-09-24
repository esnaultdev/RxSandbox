package aodev.blue.rxsandbox.ui.screen.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.observableOperators


class ListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = OperatorAdapter(requireContext(), this::onOperatorClicked)
        recyclerView.adapter = adapter

        adapter.categories = observableOperators
    }

    private fun onOperatorClicked(name: String) {
        val view = requireNotNull(view)
        val arguments = Bundle().apply { putString("operator_name", name) }
        Navigation.findNavController(view).navigate(R.id.action_see_details, arguments)
    }
}