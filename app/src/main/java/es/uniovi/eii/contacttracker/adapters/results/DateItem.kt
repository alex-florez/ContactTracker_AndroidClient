package es.uniovi.eii.contacttracker.adapters.results

import java.util.Date

/**
 * Representa un Item de Fecha dentro del Recycler View.
 */
class DateItem(
    var date: Date
) : RecycleItem() {


    val id get() = date.time

    override fun getType(): Int = TYPE_DATE
}