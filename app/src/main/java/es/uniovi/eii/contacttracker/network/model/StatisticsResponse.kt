package es.uniovi.eii.contacttracker.network.model

/**
 * Modela la respuesta de la API de estadísticas al registrar un
 * nuevo dato estadístico de la aplicación.
 */
data class StatisticsResponse(
    val registered: Boolean,
    val msg: String
)
