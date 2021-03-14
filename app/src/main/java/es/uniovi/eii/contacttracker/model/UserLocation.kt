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
}