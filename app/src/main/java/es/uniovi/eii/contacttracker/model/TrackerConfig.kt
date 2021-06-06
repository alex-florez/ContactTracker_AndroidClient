package es.uniovi.eii.contacttracker.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Representa los parámetros de configuración del rastreo.
 */
data class TrackerConfig(
        @Expose
        @SerializedName("infectivityPeriod")
        val infectivityPeriod: Int
)
