package es.uniovi.eii.contacttracker.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Clase de utilidades generales
 */
object Utils {

    /**
     * Recibe como parámetro una fecha y devuelve
     * el String formateado correspondiente.
     *
     * @param date fecha a formatear.
     * @param format string con el formato deseado.
     * @return string con la fecha ya formateada.
     */
    fun formatDate(date: Date, format: String): String {
        val formatter = SimpleDateFormat(format)
        return formatter.format(date)
    }

    /**
     * Devuelve una fecha Date correspondiente a las horas
     * y minutos pasados como parámetro.
     *
     * @param hours horas
     * @param minutes minutos
     * @return fecha Date
     */
    fun getDate(hours: Int, minutes: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hours)
        calendar.set(Calendar.MINUTE, minutes)
        calendar.set(Calendar.SECOND, 0)
        return calendar.time
    }

    /**
     * Devuelve las horas o minutos según se indique
     * de la fecha pasada como parámetro.
     */
    fun getFromDate(date: Date, select: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(select)
    }

    /**
     * Suma la cantidad indicada como parámetro de horas, minutos
     * o días a la fecha indicada como parámetro.
     */
    fun addToDate(date: Date, field:Int, quantity: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(field, quantity)
        return calendar.time
    }

}