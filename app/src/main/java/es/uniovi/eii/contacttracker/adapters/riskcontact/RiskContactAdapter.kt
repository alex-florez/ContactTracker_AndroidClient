package es.uniovi.eii.contacttracker.adapters.riskcontact

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.ItemCardRiskContactBinding
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.util.Utils

/**
 * Adapter para los items del recycler view
 * de contactos de riesgo.
 */
class RiskContactAdapter(
    private val onShowInMapClick: OnShowInMapClick
) : ListAdapter<RiskContact, RiskContactAdapter.RiskContactViewHolder>(RiskContact.DIFF_CALLBACK) {

    /**
     * Interfaz listener del Click sobre el botón
     * de mostrar el contacto de riesgo en el mapa.
     */
    interface OnShowInMapClick {
        fun onClick(riskContact: RiskContact)
    }

    /**
     * ViewHolder para los items de Contactos de Riesgo.
     */
    class RiskContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         * View Binding.
         */
        private val binding = ItemCardRiskContactBinding.bind(itemView)

        fun bindRiskContact(riskContact: RiskContact, onShowInMapClick: OnShowInMapClick) {
            binding.apply {
                txtContactDate.text = Utils.formatDate(riskContact.startDate, "dd/MM/yyyy")
                txtContactStartHour.text = Utils.formatDate(riskContact.startDate, "HH:mm")
                txtContactEndHour.text = Utils.formatDate(riskContact.endDate, "HH:mm")
                // Tiempo de exposición: x min y secs
                val exposeTimeMinSecs = Utils.getMinuteSecond(riskContact.exposeTime)
                val exposeTimeText = "${exposeTimeMinSecs[0]} min ${exposeTimeMinSecs[1]} secs"
                txtContactExposeTime.text = exposeTimeText
                val meanProxText = "${riskContact.meanProximity} m"
                txtContactMeanProximity.text = meanProxText
                val riskText = "${riskContact.riskPercent} %"
                txtHighestRiskPercentDetails.text = riskText
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiskContactViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_risk_contact, parent, false)
        return RiskContactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RiskContactViewHolder, position: Int) {
      holder.bindRiskContact(getItem(position), onShowInMapClick)
    }
}