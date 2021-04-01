package es.uniovi.eii.contacttracker.model

import es.uniovi.eii.contacttracker.util.Utils
import java.util.*

/**
 * Clase que representa una alarma de localización.
 */
class LocationAlarmData(
        val startDate: Date,
        val endDate: Date
) {

    /**
     * Fecha de creación.
     */
    val creationDate: Date = Date()

    /**
     * Devuelve un array de enteros con las Horas
     * y Minutos de la fecha de Inicio.
     */
    fun getStartTime(): Array<Int>{
        return arrayOf(Utils.getFromDate(startDate, Calendar.HOUR_OF_DAY),
                Utils.getFromDate(startDate, Calendar.MINUTE))
    }

    /**
     * Devuelve un array de enteros con las Horas
     * y Minutos de la fecha de Fin.
     */
    fun getEndTime(): Array<Int>{
        return arrayOf(Utils.getFromDate(endDate, Calendar.HOUR_OF_DAY),
                Utils.getFromDate(endDate, Calendar.MINUTE))
    }

}