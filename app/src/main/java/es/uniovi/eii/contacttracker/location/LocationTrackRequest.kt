package es.uniovi.eii.contacttracker.location

import com.google.android.gms.location.LocationRequest

/**
 * Clase que representa una solicitud de rastreo de ubicación,
 * con los parámetros de configuración correspondientes.
 */
class LocationTrackRequest(
        val minInterval: Long = defaultMinInterval,
        val fastestInterval: Long = defaultFastestInterval,
        val smallestDisplacement: Float = defaultSmallestDisplacement,
        val priority: Int = defaultPriority
){


    companion object {
        // Valores por defecto
        private const val defaultMinInterval: Long = 3000
        private const val defaultFastestInterval: Long = 3000
        private const val defaultSmallestDisplacement: Float = 0f
        private const val defaultPriority:Int = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
}