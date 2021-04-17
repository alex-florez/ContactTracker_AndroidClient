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

    /**
     * Devuelve un array con los minutos y segundos equivalentes
     * a los milisegundos pasados como parámetro.
     *
     * @param millis milisegundos a transformar.
     * @return array de enteros con los minutos y segundos.
     */
    fun getMinuteSecond(millis: Long): Array<Int> {
        val seconds = millis / 1000
        val formattedMinutes = (seconds / 60).toInt()
        val formattedSeconds = (seconds % 60).toInt()
        return arrayOf(formattedMinutes, formattedSeconds)
    }

    /**
     * Devuelve los milisegundos equivalentes a partir de los minutos
     * y segundos pasados como parámetro.
     *
     * @param minutes minutos
     * @param seconds segundos
     * @return milisegundos
     */
    fun getMillis(minutes: Int, seconds: Int): Long {
        val minsToSecs = minutes * 60
        return ((minsToSecs + seconds) * 1000).toLong()
    }
}