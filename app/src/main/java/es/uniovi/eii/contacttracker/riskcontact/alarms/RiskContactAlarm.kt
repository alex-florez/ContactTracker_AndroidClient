package es.uniovi.eii.contacttracker.riskcontact.alarms

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import es.uniovi.eii.contacttracker.util.DateUtils
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Clase que representa una alarma con una fecha y hora determinada
 * para ejecutar la comprobación de contactos de riesgo.
 */
@Parcelize
@Entity(tableName = "risk_contact_alarms")
data class RiskContactAlarm (
    @PrimaryKey(autoGenerate = true) var id: Long?,
    var startDate: Date,
    var active: Boolean
) : Parcelable {

    /**
     * Comprueba si la fecha de comienzo de la alarma es anterior a
     * la fecha actual, y de ser así, incrementa en un día la fecha.
     */
    fun updateHours() {
        if(startDate.before(Date())){
            startDate = DateUtils.addToDate(startDate, Calendar.DATE, 1)
        }

    }
}