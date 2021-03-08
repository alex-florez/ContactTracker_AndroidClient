package es.uniovi.eii.contacttracker.location

/**
 * Clase que representa una solicitud de rastreo de ubicación,
 * con los parámetros de configuración correspondientes.
 */
class LocationTrackRequest(
        val minInterval: Long,
        val smallestDisplacement: Float,
        val priority: Int
)