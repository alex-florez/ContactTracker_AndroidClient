package es.uniovi.eii.contacttracker.util

import java.text.SimpleDateFormat
import java.util.Date

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
}