package es.uniovi.eii.contacttracker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Clase de datos que representa un punto en el mapa
 * con coordenadas (latitud, longitud) y una fecha con
 * horas, minutos y segundos.
 */
@Parcelize
data class Point(
    val lat: Double,
    val lng: Double,
    val timestamp: Date
) : Parcelable {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point

        if (lat != other.lat) return false
        if (lng != other.lng) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lat.hashCode()
        result = 31 * result + lng.hashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }

    override fun toString(): String {
        return "Point(lat=$lat, lng=$lng, timestamp=$timestamp)"
    }

}
