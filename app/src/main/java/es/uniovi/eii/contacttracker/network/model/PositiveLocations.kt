package es.uniovi.eii.contacttracker.network.model

import es.uniovi.eii.contacttracker.model.UserLocation
import java.util.Date

/**
 * Clase de datos que representa las localizaciones registradas
 * por el dispositivo de un usuario que ha notificado ser positivo.
 */
data class PositiveLocations(
        val locations: List<UserLocation>, // Localizaciones
        val locationDates: List<Date> // Lista de fechas correspondientes a las localizaciones
)
