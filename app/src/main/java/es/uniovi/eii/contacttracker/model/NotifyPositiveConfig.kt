package es.uniovi.eii.contacttracker.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import es.uniovi.eii.contacttracker.Constants

/* Valores por defecto */
const val DEFAULT_INFECTIVITY_PERIOD = 3

/**
 * Representa las opciones de configuración
 * para la notificación de positivos.
 */
data class NotifyPositiveConfig(

    /* Periodo de infectividad: n.º de días atrás que se tienen en cuenta a la hora de subir las
     * localizaciones del usuario que da positivo.
     */
    @Expose
    @SerializedName("infectivityPeriod")
    val infectivityPeriod: Int = DEFAULT_INFECTIVITY_PERIOD
)