package es.uniovi.eii.contacttracker.adapters.results

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.ItemCardRiskContactResultBinding
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.util.DateUtils

/**
 * Callback para comprobar las diferencias entre objetos RiskContactResult.
 */
class RiskContactResultDiffCallback : DiffUtil.ItemCallback<RiskContactResult>() {

    override fun areItemsTheSame(oldItem: RiskContactResult, newItem: RiskContactResult): Boolean {
        if(oldItem.resultId == null || newItem.resultId == null)
            return false
        return oldItem.resultId == newItem.resultId
    }

    override fun areContentsTheSame(
        oldItem: RiskContactResult,
        newItem: RiskContactResult
    ): Boolean {
        return oldItem == newItem
    }
}

/* Instancia del DIFF Callback */
private val DIFF_CALLBACK = RiskContactResultDiffCallback()

/**
 * Adapter para los items que representan el Resultado
 * de los Contactos de Riesgo.
 */
class RiskContactResultAdapter(
    private val onRiskContactResultClick: OnRiskContactResultClick
): ListAdapter<RiskContactResult, RiskContactResultAdapter.RiskContactResultViewHolder>(DIFF_CALLBACK) {

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
            binding.apply {
                txtResultDate.text = DateUtils.formatDate(riskContactResult.timestamp, "dd/MM/yyyy")
                txtResultHour.text = DateUtils.formatDate(riskContactResult.timestamp, "HH:mm")
                txtNumberOfPositives.text = binding.root.context.resources
                    .getQuantityString(R.plurals.positivesText, riskContactResult.numberOfPositives,
                        riskContactResult.numberOfPositives)
                // Porcentaje de riesgo más alto
                txtHighestRiskPercent.text = binding.root.context
                    .getString(R.string.percentText, riskContactResult.getHighestRiskContact().riskPercent)
            }
            // Listener de Click.
            itemView.setOnClickListener{
                onRiskContactResultClick.onClick(riskContactResult)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiskContactResultViewHolder {
        val itemView = LayoutInflater.from(parent.context)
           .inflate(R.layout.item_card_risk_contact_result, parent, false)
        return RiskContactResultViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RiskContactResultViewHolder, position: Int) {
        holder.bindRiskContactResult(getItem(position), onRiskContactResultClick)
    }

    fun addResult(riskContactResult: RiskContactResult) {
        val newList = currentList.toMutableList()
        newList.add(0, riskContactResult)
        submitList(newList)
    }

}