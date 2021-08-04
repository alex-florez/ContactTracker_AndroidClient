package es.uniovi.eii.contacttracker.adapters.results

import es.uniovi.eii.contacttracker.model.RiskContactResult

/**
 * Representa un objeto con datos dentro del Recycler View.
 */
class ObjectItem(
    var riskContactResult: RiskContactResult
) : RecycleItem() {

    override fun getType(): Int = TYPE_OBJECT
}