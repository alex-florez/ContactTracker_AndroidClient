package es.uniovi.eii.contacttracker.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Clase que contiene los datos recibidos en la
 * respuesta del Backend al notificar un positivo.
 */
data class NotifyPositiveResult(
        @Expose
        @SerializedName("uploadedLocations")
        var uploadedLocations: Int
)