package es.uniovi.eii.contacttracker.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Clase que contiene los datos recibidos en la
 * respuesta del Backend al notificar un positivo.
 */
data class NotifyPositiveResponse(

        /* Código único del Positivo */
        @Expose
        @SerializedName("positiveCode")
        var positiveCode: String? = null,

        /* Número de localizaciones registradas */
        @Expose
        @SerializedName("uploadedLocations")
        var uploadedLocations: Int,
)