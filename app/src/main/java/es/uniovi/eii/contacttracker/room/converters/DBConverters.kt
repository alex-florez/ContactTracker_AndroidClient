package es.uniovi.eii.contacttracker.room.converters

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Clase que utiliza ROOM para convertir y parsear datos
 * entre la App y la base de datos SQLite.
 */
class DBConverters {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")


    @TypeConverter
    fun dateFromString(dateString: String): Date {
        return dateFormatter.parse(dateString) ?: Date()
    }

    @TypeConverter
    fun dateToString(date: Date): String {
        return dateFormatter.format(date)
    }
}
