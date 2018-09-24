package aodev.blue.rxsandbox.ui.screen.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import aodev.blue.rxsandbox.R
import aodev.blue.rxsandbox.model.OperatorCategory
import kotlin.properties.Delegates


class OperatorAdapter(
        context: Context,
        private val onOperatorClicked: (name: String) -> Unit
) : RecyclerView.Adapter<OperatorAdapter.ViewHolder>() {

    private var flattenOperatorNames = emptyList<String>()
    var categories: List<OperatorCategory> by Delegates.observable(emptyList()) {
        _, oldValue, newValue ->
        if (oldValue != newValue) {
            flattenOperatorNames = newValue.flatMap { it.operatorNames }
            notifyDataSetChanged()
        }
    }

    private val layoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = flattenOperatorNames.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.item_operator, parent, false)
        return ViewHolder(view, onOperatorClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val operatorName = flattenOperatorNames[position]
        holder.bind(operatorName)
    }

    class ViewHolder(
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
}