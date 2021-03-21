package es.uniovi.eii.contacttracker.room.converters

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Clase que utiliza ROOM para convertir y parsear datos
 * entre la App y la base de datos SQLite.
 */
class DBConverters {

    @TypeConverter
    fun dateFromString(dateString: String): Date {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateFormatter.parse(dateString) ?: Date()
    }

    @TypeConverter
    fun dateToString(date: Date): String {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return dateFormatter.format(date)
    }
}
