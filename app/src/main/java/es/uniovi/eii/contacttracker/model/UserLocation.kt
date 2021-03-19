package es.uniovi.eii.contacttracker.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import es.uniovi.eii.contacttracker.adapters.diffutil.UserLocationDiffCallback
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Clase de datos UserLocation que representa el concepto
 * de Localización del Usuario.
 */
@Parcelize
data class UserLocation (
    val lat: Double,
    val lng: Double,
    val accuracy: Double,
    val provider: String,
    val timestamp: Date
) : Parcelable {


    companion object {
        /**
         * Variable estática que almacena el callback de diferencias.
         */
        val DIFF_CALLBACK: DiffUtil.ItemCallback<UserLocation> = UserLocationDiffCallback()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserLocation

        if (lat != other.lat) return false
        if (lng != other.lng) return false
        if (accuracy != other.accuracy) return false
        if (provider != other.provider) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lat.hashCode()
        result = 31 * result + lng.hashCode()
        result = 31 * result + accuracy.hashCode()
        result = 31 * result + provider.hashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }
}