package es.uniovi.eii.contacttracker.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import es.uniovi.eii.contacttracker.adapters.locations.UserLocationDiffCallback
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Clase de datos que representa el concepto
 * de una localización/ubicación del usuario.
 */
@Parcelize
@Entity(tableName = "user_locations")
data class UserLocation (
    @PrimaryKey(autoGenerate = true) var userlocationID: Long?,
    @Embedded val point: Point,
    val accuracy: Double,
    val provider: String
) : Parcelable {

    companion object {
        /**
         * Variable estática que almacena el callback de diferencias.
         */
        val DIFF_CALLBACK: DiffUtil.ItemCallback<UserLocation> = UserLocationDiffCallback()
    }

    /* Devuelve la latitud */
    fun lat(): Double = point.lat

    /* Devuelve la longitud */
    fun lng(): Double = point.lng

    /* Devuelve el timestamp de la localización */
    fun timestamp(): Date = point.timestamp

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserLocation

        if (userlocationID != other.userlocationID) return false
        if (point != other.point) return false
        if (accuracy != other.accuracy) return false
        if (provider != other.provider) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userlocationID?.hashCode() ?: 0
        result = 31 * result + point.hashCode()
        result = 31 * result + accuracy.hashCode()
        result = 31 * result + provider.hashCode()
        return result
    }

    override fun toString(): String {
        return "UserLocation(id=$userlocationID, point=$point, accuracy=$accuracy, provider='$provider')"
    }

}