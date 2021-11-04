package es.uniovi.eii.contacttracker.riskcontact.alarms

import android.os.Parcel
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
@Entity(tableName = "risk_contact_alarms")
class RiskContactAlarm (
    @PrimaryKey(autoGenerate = true) var id: Long?,
    var startDate: Date,
    var active: Boolean
) : Parcelable {

    private constructor(parcel: Parcel) : this (
        parcel.readValue(Long::class.java.classLoader) as? Long,
        Date(parcel.readLong()),
        parcel.readByte() != 0.toByte()
    )

    /**
     * Comprueba si la fecha de comienzo de la alarma es anterior a
     * la fecha actual, y de ser así, incrementa en un día la fecha.
     *
     * También establece los segundos de la fecha a 0.
     */
    fun updateHours() {
        if(startDate.before(Date())){
            startDate = DateUtils.addToDate(startDate, Calendar.DATE, 1)
        }
        // Eliminar segundos
        val cal = Calendar.getInstance()
        cal.time = startDate
        cal.set(Calendar.SECOND, 0)
        startDate = cal.time
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeLong(startDate.time)
        parcel.writeByte(if (active) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RiskContactAlarm> {
        override fun createFromParcel(parcel: Parcel): RiskContactAlarm {
            return RiskContactAlarm(parcel)
        }

        override fun newArray(size: Int): Array<RiskContactAlarm?> {
            return arrayOfNulls(size)
        }
    }
}