package es.uniovi.eii.contacttracker.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import es.uniovi.eii.contacttracker.Constants

/* Valores por defecto */
const val DEFAULT_MIN_INTERVAL = 3000L
const val DEFAULT_SMALLEST_DISPLACEMENT = 0f

/* Claves de las Shared Preferences */
const val TRACKER_MIN_INTERVAL_KEY = "TrackerMinInterval"
const val TRACKER_SMALLEST_DISPLACEMENT_KEY = "TrackerSmallestDisplacementKey"

/**
 * Representa los parámetros de configuración del rastreo de ubicación.
 */
data class TrackerConfig(
        /* Intervalo de tiempo mínimo entre cada actualización de ubicación */
        val minInterval: Long = DEFAULT_MIN_INTERVAL,

        /* Desplazamiento mínimo requerido para recibir una nueva actualización de ubicación */
        val smallestDisplacement: Float = DEFAULT_SMALLEST_DISPLACEMENT
)
