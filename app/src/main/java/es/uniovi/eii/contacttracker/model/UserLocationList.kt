package es.uniovi.eii.contacttracker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserLocationList(
    val locations: List<UserLocation>?
) : Parcelable {

    /**
     * Devuelve la primera localización registrada en el tiempo.
     */
    fun getInitialLocation(): UserLocation? {
        if(locations != null && locations.isNotEmpty())
            return locations[locations.size-1]
        return null
    }
}