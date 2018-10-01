package aodev.blue.rxsandbox.ui.screen.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.OperatorCategory
import aodev.blue.rxsandbox.utils.exhaustive
import aodev.blue.rxsandbox.utils.linkedListOf
import java.lang.IllegalArgumentException
import kotlin.properties.Delegates


class OperatorAdapter(
        context: Context,
        private val onOperatorClicked: (name: String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private object ViewType {
        const val OPERATOR = 0
        const val CATEGORY = 1
    }

    class AdapterItem(val isCategory: Boolean, val name: String)

    private var flattenedItems = emptyList<AdapterItem>()
    var categories: List<OperatorCategory> by Delegates.observable(emptyList()) {
        _, oldValue, newValue ->
        if (oldValue != newValue) {
            flattenedItems = newValue.flatMap { category ->
                val operatorItems = category.operatorNames.map { AdapterItem(false, it) }
                val categoryItem = AdapterItem(true, category.name)
                linkedListOf(operatorItems).apply { push(categoryItem) }
            }
            notifyDataSetChanged()
        }
    }

    private val layoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = flattenedItems.size

    override fun getItemViewType(position: Int): Int {
        return if (flattenedItems[position].isCategory) {
            ViewType.CATEGORY
        } else {
            ViewType.OPERATOR
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.OPERATOR -> {
                val view = layoutInflater.inflate(R.layout.item_list_operator, parent, false)
                OperatorViewHolder(view, onOperatorClicked)
            }
            ViewType.CATEGORY -> {
                val view = layoutInflater.inflate(R.layout.item_list_category, parent, false)
                CategoryViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OperatorViewHolder -> {
                val operatorName = flattenedItems[position].name
                holder.bind(operatorName)
            }
            is CategoryViewHolder -> {
                val categoryName = flattenedItems[position].name
                holder.bind(categoryName)
            }
            else -> throw IllegalArgumentException("Invalid view holder $holder")
        }.exhaustive
    }

    // region View holders

    private class OperatorViewHolder(
            itemView: View,
            private val onOperatorClicked: (name: String) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val nameTextView: TextView = itemView.findViewById(R.id.operator_name)
        private var name: String? = null

        init {
            itemView.setOnClickListener {
                name?.let { onOperatorClicked(it) }
            }
        }

        fun bind(name: String) {
            this.name = name
            nameTextView.text = name
        }
    }

    private class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameTextView: TextView = itemView.findViewById(R.id.category_name)

        fun bind(name: String) {
            nameTextView.text = name
        }
    }

    // endregion
}