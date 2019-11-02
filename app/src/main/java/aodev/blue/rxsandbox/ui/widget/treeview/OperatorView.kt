package aodev.blue.rxsandbox.ui.widget.treeview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import aodev.blue.rxsandbox.R
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

    init {
        val layoutInflater = LayoutInflater.from(context)
        layoutInflater.inflate(R.layout.widget_operator, this, true)

        nameView = findViewById(R.id.operator_name)

        setOnClickListener { onShowOperatorInfoClick() }

        if (isInEditMode) {
            text = "map { x -> x * 2 }"
        }
    }

    var text: String? by Delegates.observable<String?>(null) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            nameView.text = text
        }
    }

    var docUrl: String? = null

    private fun onShowOperatorInfoClick() {
        // TODO use a listener to handle this
        docUrl?.let(context::openInBrowser)
    }
}
