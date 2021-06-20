package es.uniovi.eii.contacttracker.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import es.uniovi.eii.contacttracker.Constants

/**
 * Representa los parámetros de configuración del rastreo.
 */
data class TrackerConfig(
        @Expose
        @SerializedName("infectivityPeriod")
        val infectivityPeriod: Int = Constants.DEFAULT_INFECTIVITY_PERIOD
)
