package aodev.blue.rxsandbox.ui.widget.asynctree

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.ui.utils.extension.getColorCompat
import com.google.android.material.card.MaterialCardView
import kotlin.properties.Delegates


class OperatorView : MaterialCardView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    // Resources
    private val padding = context.resources.getDimension(R.dimen.operator_padding)
    private val nameTextSize = context.resources.getDimension(R.dimen.operator_name_text_size)

    // Sub views
    private val textView: TextView

    init {
        textView = TextView(context).apply {
            gravity = Gravity.CENTER

            val padding = padding.toInt()
            setPadding(padding, padding, padding, padding)

            setTextColor(context.getColorCompat(R.color.operator_text_color))
            setTextSize(TypedValue.COMPLEX_UNIT_PX, nameTextSize)
        }
        addView(textView,
                FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                )
        )

        if (isInEditMode) {
            text = "map { x -> x * 2 }"
        }
    }

    var text: String? by Delegates.observable<String?>(null) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            textView.text = text
        }
    }
}
