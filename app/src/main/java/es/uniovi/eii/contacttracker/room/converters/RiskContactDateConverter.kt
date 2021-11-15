package es.uniovi.eii.contacttracker.room.converters

import android.util.Log
import androidx.room.TypeConverter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * Convertidor de fechas a timestamps en milisegunos para las
 * fechas de inicio y de fin de los contactos de riesgo.
 */
class RiskContactDateConverter {

    @TypeConverter
    fun toDate(millis: Long?): Date? {
        if(millis != null)
            return Date(millis)
        return null
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        if(date != null){
            return date.time.toLong()
        }
        return null
    }
}