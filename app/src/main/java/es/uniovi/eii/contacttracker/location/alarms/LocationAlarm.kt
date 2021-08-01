package es.uniovi.eii.contacttracker.location.alarms

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import es.uniovi.eii.contacttracker.util.DateUtils
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Clase que representa una alarma de localización.
 */
@Parcelize
@Entity(tableName = "location_alarms")
data class LocationAlarm(
        @PrimaryKey(autoGenerate = true) val id: Long?,
        var startDate: Date,
        var endDate: Date,
        var active: Boolean
) : Parcelable {

    /**
     * Fecha de creación.
     */
    var creationDate: Date = Date()

    /**
     * Devuelve un array de enteros con las Horas
     * y Minutos de la fecha de Inicio.
     */
    fun getStartTime(): Array<Int>{
        return arrayOf(DateUtils.getFromDate(startDate, Calendar.HOUR_OF_DAY),
                DateUtils.getFromDate(startDate, Calendar.MINUTE))
    }

    /**
     * Devuelve un array de enteros con las Horas
     * y Minutos de la fecha de Fin.
     */
    fun getEndTime(): Array<Int>{
        return arrayOf(DateUtils.getFromDate(endDate, Calendar.HOUR_OF_DAY),
                DateUtils.getFromDate(endDate, Calendar.MINUTE))
    }


    /**
     * Comprueba las horas de INICIO y de FIN de la alarma. Si
     * las horas están desfasadas con respecto la fecha actual, las actualiza para que
     * pasen a ser programadas un día más tarde.
     */
    fun updateHours() {
        if(startDate.before(Date())){ // alarma desfasada
            startDate = DateUtils.addToDate(startDate, Calendar.DATE, 1)
            endDate = DateUtils.addToDate(endDate, Calendar.DATE, 1)
        }
    }

    /**
     * Comprueba la validez de las horas de inicio
     * y de fin de la alarma.
     *
     * @return true si la alarma de localización es válida.
     */
    fun isValid(): Boolean {
        return startDate.before(endDate)
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
}