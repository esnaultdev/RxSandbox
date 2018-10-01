package aodev.blue.rxsandbox.ui.screen.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.sample.getObservableSample
import aodev.blue.rxsandbox.ui.screen.NavigationLabelListener
import aodev.blue.rxsandbox.ui.widget.operator.OperatorView


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

        (activity as? NavigationLabelListener)?.updateLabel(operatorName)

        val sample = getObservableSample(operatorName)
        if (sample != null) {
            val operatorView: OperatorView = view.findViewById(R.id.details_operator)
            operatorView.text = sample.operator.expression()
        } else {
            TODO("Properly handle a missing operator")
        }
    }
}
