package es.uniovi.eii.contacttracker.room.converters

import androidx.room.TypeConverter
import es.uniovi.eii.contacttracker.model.RiskLevel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Clase de apoyo a ROOM para convertir y parsear datos
 * entre la App y la base de datos SQLite.
 */
class DBConverters {

    private val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    /* FECHAS */
    @TypeConverter
    fun dateFromString(dateString: String): Date {
        return df.parse(dateString) ?: Date()
    }

    @TypeConverter
    fun dateToString(date: Date): String {
        return df.format(date)
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
