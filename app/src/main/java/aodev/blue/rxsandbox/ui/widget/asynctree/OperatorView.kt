package aodev.blue.rxsandbox.ui.widget.asynctree

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.ui.utils.extension.setVisible
import aodev.blue.rxsandbox.utils.openInBrowser
import com.google.android.material.card.MaterialCardView
import kotlin.properties.Delegates


class OperatorView : MaterialCardView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    // Sub views
    private val nameView: TextView
    private val infoView: ImageView

    init {
        val layoutInflater = LayoutInflater.from(context)
        layoutInflater.inflate(R.layout.widget_operator, this, true)

        nameView = findViewById(R.id.operator_name)
        infoView = findViewById(R.id.operator_info)

        infoView.setOnClickListener { onInfoIconClick() }

        if (isInEditMode) {
            text = "map { x -> x * 2 }"
        }
    }

    var text: String? by Delegates.observable<String?>(null) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            nameView.text = text
        }
    }

    var docUrl: String? by Delegates.observable<String?>(null) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            infoView.setVisible(newValue != null)
        }
    }

    private fun onInfoIconClick() {
        // TODO use a listener to handle this
        docUrl?.let(context::openInBrowser)
    }
}
