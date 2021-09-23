package es.uniovi.eii.contacttracker.room.converters

import androidx.room.TypeConverter
import es.uniovi.eii.contacttracker.model.RiskLevel
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * Clase de apoyo a ROOM para convertir y parsear datos
 * entre la App y la base de datos SQLite.
 */
class DBConverters {

    private val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    /* FECHAS */
    @TypeConverter
    fun dateFromString(dateString: String): Date? {
        return try {
            df.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun dateToString(date: Date?): String {
        if(date != null)
            return df.format(date)
        return ""
    }

    @TypeConverter
    fun toRiskLevel(riskLevel: String): RiskLevel {
        return enumValueOf(riskLevel)
    }

    @TypeConverter
    fun fromRiskLevel(riskLevel: RiskLevel): String {
        return riskLevel.name
    }


}
