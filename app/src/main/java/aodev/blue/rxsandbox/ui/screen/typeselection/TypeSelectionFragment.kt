package aodev.blue.rxsandbox.ui.screen.typeselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.TimelineType


class TypeSelectionFragment : Fragment() {

    private val navController
        get() = findNavController()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_type_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.type_selection_observable_button).setOnClickListener {
            onTypeClicked(TimelineType.OBSERVABLE)
        }
        view.findViewById<Button>(R.id.type_selection_single_button).setOnClickListener {
            onTypeClicked(TimelineType.SINGLE)
        }
        view.findViewById<Button>(R.id.type_selection_maybe_button).setOnClickListener {
            onTypeClicked(TimelineType.MAYBE)
        }
        view.findViewById<Button>(R.id.type_selection_completable_button).setOnClickListener {
            onTypeClicked(TimelineType.COMPLETABLE)
        }
    }

    private fun onTypeClicked(type: TimelineType) {
        val arguments = Bundle().apply { putInt("timeline_type", type.ordinal) }
        navController.navigate(R.id.action_select_type, arguments)
    }
}