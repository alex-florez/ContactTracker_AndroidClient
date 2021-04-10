package es.uniovi.eii.contacttracker.model

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import es.uniovi.eii.contacttracker.adapters.alarms.LocationAlarmDiffCallback
import es.uniovi.eii.contacttracker.util.Utils
import java.util.*

/**
 * Clase que representa una alarma de localización.
 */
@Entity(tableName = "location_alarms")
class LocationAlarm(
        @PrimaryKey(autoGenerate = true) val id: Long?,
        var startDate: Date,
        var endDate: Date,
        var active: Boolean
) {

    /**
     * Fecha de creación.
     */
    var creationDate: Date = Date()

    /**
     * Devuelve un array de enteros con las Horas
     * y Minutos de la fecha de Inicio.
     */
    fun getStartTime(): Array<Int>{
        return arrayOf(Utils.getFromDate(startDate, Calendar.HOUR_OF_DAY),
                Utils.getFromDate(startDate, Calendar.MINUTE))
    }

    /**
     * Devuelve un array de enteros con las Horas
     * y Minutos de la fecha de Fin.
     */
    fun getEndTime(): Array<Int>{
        return arrayOf(Utils.getFromDate(endDate, Calendar.HOUR_OF_DAY),
                Utils.getFromDate(endDate, Calendar.MINUTE))
    }

    override fun toString(): String {
        return "LocationAlarm(id=$id, startDate=$startDate, endDate=$endDate, active=$active, creationDate=$creationDate)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocationAlarm

        if (id != other.id) return false
        if (startDate != other.startDate) return false
        if (endDate != other.endDate) return false
        if (active != other.active) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + startDate.hashCode()
        result = 31 * result + endDate.hashCode()
        result = 31 * result + active.hashCode()
        return result
    }

    companion object {
        // Vble estática que almacena el callback de diferencias
        val DIFF_CALLBACK: DiffUtil.ItemCallback<LocationAlarm> = LocationAlarmDiffCallback()
    }
}