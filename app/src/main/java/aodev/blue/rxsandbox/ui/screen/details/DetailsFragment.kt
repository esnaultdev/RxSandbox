package aodev.blue.rxsandbox.ui.screen.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.TimelineType
import aodev.blue.rxsandbox.model.sample.getSample
import aodev.blue.rxsandbox.ui.screen.NavigationLabelListener
import aodev.blue.rxsandbox.ui.widget.treeview.SingleColumnTreeView


class DetailsFragment : Fragment() {

    // Arguments
    private val operatorName: String by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getString("operator_name") ?: ""
    }
    private val timelineType: TimelineType by lazy(LazyThreadSafetyMode.NONE) {
        val streamTypeIndex = arguments?.getInt("timeline_type", 0) ?: 0
        TimelineType.values()[streamTypeIndex]
    }

    // UI
    private lateinit var asyncTreeView: SingleColumnTreeView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        asyncTreeView = view.findViewById(R.id.async_tree_view)

        (activity as? NavigationLabelListener)?.updateLabel(operatorName)

        val sample = getSample(timelineType, operatorName)

        if (sample != null) {
            asyncTreeView.reactiveTypeX = sample
        } else {
            TODO("Properly handle a missing operator")
        }
    }
}
