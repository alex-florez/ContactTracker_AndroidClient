package es.uniovi.eii.contacttracker.adapters.riskcontact

import androidx.recyclerview.widget.DiffUtil
import es.uniovi.eii.contacttracker.model.RiskContact

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