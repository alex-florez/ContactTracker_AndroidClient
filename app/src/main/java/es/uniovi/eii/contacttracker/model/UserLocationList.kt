package es.uniovi.eii.contacttracker.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import es.uniovi.eii.contacttracker.util.LocationUtils
import kotlinx.parcelize.Parcelize

/**
 * Clase parcelable que envuelve a la lista de localizaciones
 * de usuario.
 */
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


    /**
     * Convierte la lista de localizaciones del usuario
     * en una lista de objetos LatLng.
     *
     * @return lista de LatLng.
     */
    fun getAsLatLng(): List<LatLng> {
        val latLngList = mutableListOf<LatLng>()
        locations?.forEach {
            latLngList.add(LocationUtils.toLatLng(it))
        }
        return latLngList
    }
}