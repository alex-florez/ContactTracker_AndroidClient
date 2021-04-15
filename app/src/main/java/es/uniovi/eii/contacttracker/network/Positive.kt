package es.uniovi.eii.contacttracker.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Clase que representa un positivo de COVID-19,
 * y que contiene las localizaciones corresondientes
 * registradas por el dispositivo.
 */
data class Positive(
    @Expose
    @SerializedName("positiveID")
    var id: Long,
    @Expose
    @SerializedName("prueba")
    var prueba: String
) {
    override fun toString(): String {
        return "Positive(id=$id, prueba='$prueba')"
    }
}
