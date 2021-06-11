package es.uniovi.eii.contacttracker.room.converters

import androidx.room.TypeConverter
import es.uniovi.eii.contacttracker.model.RiskLevel
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Clase que utiliza ROOM para convertir y parsear datos
 * entre la App y la base de datos SQLite.
 */
class DBConverters {

    /* FECHAS */
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

    @TypeConverter
    fun toRiskLevel(riskLevel: String): RiskLevel {
        return enumValueOf<RiskLevel>(riskLevel)
    }

    @TypeConverter
    fun fromRiskLevel(riskLevel: RiskLevel): String {
        return riskLevel.name
    }


}
