package es.uniovi.eii.contacttracker.adapters.results

import androidx.recyclerview.widget.DiffUtil
import es.uniovi.eii.contacttracker.model.RiskContactResult

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