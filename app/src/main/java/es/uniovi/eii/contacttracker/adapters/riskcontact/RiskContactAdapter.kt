package es.uniovi.eii.contacttracker.adapters.riskcontact

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.ItemCardRiskContactBinding
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.model.RiskLevel
import es.uniovi.eii.contacttracker.util.DateUtils
import java.sql.Date

/**
 * Callback de diferencias para los contactos
 * de riesgo, utilizado en el ListAdapter.
 */
class RiskContactDiffCallback : DiffUtil.ItemCallback<RiskContact>() {
    override fun areContentsTheSame(oldItem: RiskContact, newItem: RiskContact): Boolean {
        if(oldItem.riskContactId == null || newItem.riskContactId == null)
            return false
        return oldItem.riskContactId == newItem.riskContactId
    }

    override fun areItemsTheSame(oldItem: RiskContact, newItem: RiskContact): Boolean {
        return oldItem == newItem
    }
}

/* Constantes */
private val DIFF_CALLBACK = RiskContactDiffCallback() // Instancia del DIFF Callback

/**
 * Adapter para los items del recycler view
 * de contactos de riesgo.
 */
class RiskContactAdapter(
    private val onShowInMapClick: OnShowInMapClick
) : ListAdapter<RiskContact, RiskContactAdapter.RiskContactViewHolder>(DIFF_CALLBACK) {

    /**
     * Interfaz Listener de Click para mostrar el contacto de riesgo en el mapa.
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

        /**
         * Vincula los datos del contacto de riesgo indicado con
         * los componentes del Card.
         */
        fun bindRiskContact(riskContact: RiskContact, onShowInMapClick: OnShowInMapClick) {
            binding.du = DateUtils
            binding.rc = riskContact
            setListeners(riskContact, onShowInMapClick)
        }

        /**
         * Establece los listeners de eventos para los
         * componentes del Card.
         */
        private fun setListeners(riskContact: RiskContact, onShowInMapClick: OnShowInMapClick) {
            binding.btnShowContactInMap.setOnClickListener {
                onShowInMapClick.onClick(riskContact)
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