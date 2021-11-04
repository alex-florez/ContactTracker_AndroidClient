package es.uniovi.eii.contacttracker.adapters.results

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.ItemCardRiskContactResultBinding
import es.uniovi.eii.contacttracker.databinding.ItemDateRecyclerBinding
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.util.DateUtils
import java.util.Date

/**
 * Callback para comprobar las diferencias entre objetos RecyclerItem.
 *
 * Estos objetos pueden ser instancias de DateItem o de ObjectItem.
 */
class RiskContactResultDiffCallback : DiffUtil.ItemCallback<RecycleItem>() {

    override fun areItemsTheSame(oldItem: RecycleItem, newItem: RecycleItem): Boolean {
        if(oldItem is DateItem && newItem is DateItem) {
            return oldItem.id == newItem.id
        } else if (oldItem is ObjectItem && newItem is ObjectItem) {
            if(oldItem.riskContactResult.resultId != null && newItem.riskContactResult.resultId != null){
                return oldItem.riskContactResult.resultId == newItem.riskContactResult.resultId
            }
            return false
        } else {
            return false
        }
    }

    override fun areContentsTheSame(oldItem: RecycleItem, newItem: RecycleItem): Boolean {
        return if(oldItem is DateItem && newItem is DateItem) {
            oldItem.date == newItem.date
        } else if (oldItem is ObjectItem && newItem is ObjectItem) {
            oldItem.riskContactResult == newItem.riskContactResult
        } else {
            false
        }
    }
}

/* Instancia del DIFF Callback */
private val DIFF_CALLBACK = RiskContactResultDiffCallback()

/**
 * Adapter para los items que representan el Resultado de los Contactos de Riesgo.
 */
class RiskContactResultAdapter(
    private val onRiskContactResultClick: OnRiskContactResultClick
): ListAdapter<RecycleItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    /**
     * Interfaz listener para el click sobre un resultado de contactos de riesgo.
     */
    interface OnRiskContactResultClick {
        fun onClick(riskContactResult: RiskContactResult)
    }

    /**
     * ViewHolder para los objetos RiskContactResult.
     */
    class RiskContactResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * View Binding.
         */
        private val binding = ItemCardRiskContactResultBinding.bind(itemView)

        fun bindRiskContactResult(riskContactResult: RiskContactResult, onRiskContactResultClick: OnRiskContactResultClick) {
            binding.result = riskContactResult
            binding.du = DateUtils
            // Listener de Click.
            itemView.setOnClickListener{
                onRiskContactResultClick.onClick(riskContactResult)
            }
        }
    }

    /**
     * ViewHolder para las fechas de los resultados.
     */
    class DateViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        /* View Binding */
        private val binding = ItemDateRecyclerBinding.bind(itemView)

        /**
         * Vincula la fecha pasada como parámetro con la vista.
         */
        fun bindDate(date: Date) {
            val dateFormat = "EEEE, dd 'de' MMMM, yyyy"
            val text = DateUtils.formatDate(date, dateFormat)
            binding.resultDate.text = text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        lateinit var vh: RecyclerView.ViewHolder
        when(viewType) {
            TYPE_DATE -> { // Elemento de Fecha
                val view = inflater.inflate(R.layout.item_date_recycler, parent, false)
                vh = DateViewHolder(view)
            }
            TYPE_OBJECT -> { // Elemento con un resultado
                val view = inflater.inflate(R.layout.item_card_risk_contact_result, parent, false)
                vh = RiskContactResultViewHolder(view)
            }
        }
        return vh
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when(viewHolder.itemViewType) {
            TYPE_DATE -> {
                val dateItem = currentList[position] as DateItem
                val dateVH = viewHolder as DateViewHolder
                dateVH.bindDate(dateItem.date)
            }
            TYPE_OBJECT -> {
                val objectItem = currentList[position] as ObjectItem
                val resultVH = viewHolder as RiskContactResultViewHolder
                resultVH.bindRiskContactResult(objectItem.riskContactResult, onRiskContactResultClick)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return currentList[position].getType()
    }
}