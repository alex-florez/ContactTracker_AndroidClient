package es.uniovi.eii.contacttracker.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * Clase de utilidades generales
 */
object DateUtils {

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
     * Convierte los milisegundos pasados como parámetro en un
     * objeto Date teniendo en cuenta la zona horaria del dispositivo.
     *
     * @param millis Milisegundos.
     * @return Objeto Date correspondiente.
     */
    fun millisToDate(millis: Long): Date {
        val time = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(millis), ZoneId.systemDefault() // Zona horaria del dispositivo.
        )
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant())
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

    /**
     * Devuelve la diferencia de tiempo en SEGUNDOS entre las dos
     * fechas pasadas como parámetro.
     *
     * @param date1 Fecha 1.
     * @param date2 Fecha 2.
     * @return Número de segundos de diferencia.
     */
    fun dateDifferenceInSecs(date1: Date, date2: Date): Int {
        val diff = abs(date1.time - date2.time)
        return (diff / 1000).toInt()
    }

    /**
     * Transforma el string pasado como parámetro a una
     * instancia de la Clase Date según el formato indicado
     * como parámetro.
     *
     * @param stringDate String con la fecha.
     * @param format Formato de la fecha.
     * @return Objeto Date.
     */
    fun toDate(stringDate: String, format: String): Date? {
        val formatter = SimpleDateFormat(format)
        return formatter.parse(stringDate)
    }
}