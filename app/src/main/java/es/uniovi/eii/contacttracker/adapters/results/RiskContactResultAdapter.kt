package es.uniovi.eii.contacttracker.adapters.results

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.ItemCardRiskContactResultBinding
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.util.Utils

/**
 * Adapter para los items que representan el Resultado
 * de los Contactos de Riesgo.
 */
class RiskContactResultAdapter(
    private val onRiskContactResultClick: OnRiskContactResultClick
): ListAdapter<RiskContactResult, RiskContactResultAdapter.RiskContactResultViewHolder>(RiskContactResult.DIFF_CALLBACK) {

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
                txtResultDate.text = Utils.formatDate(riskContactResult.timestamp, "dd/MM/yyyy")
                txtResultHour.text = Utils.formatDate(riskContactResult.timestamp, "HH:mm")
                val positivesString = if(riskContactResult.numberOfPositives > 1
                                        || riskContactResult.numberOfPositives == 0) "${riskContactResult.numberOfPositives} positivos"
                                        else "${riskContactResult.numberOfPositives} positivo"
                txtNumberOfPositives.text = positivesString
                txtHighestRiskPercent.text = "12 %"
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