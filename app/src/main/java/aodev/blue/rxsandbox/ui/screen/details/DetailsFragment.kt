package aodev.blue.rxsandbox.ui.screen.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.ui.screen.NavigationLabelListener


class DetailsFragment : Fragment() {

    private val operatorName: String by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.getString("operator_name") ?: ""
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val operatorNameTextView: TextView = view.findViewById(R.id.operator_name)
        operatorNameTextView.text = operatorName

        (activity as? NavigationLabelListener)?.updateLabel(operatorName)
    }
}
