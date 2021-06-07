package es.uniovi.eii.contacttracker.model

import es.uniovi.eii.contacttracker.model.UserLocation
import java.util.Date

/**
 * Clase de datos que representa a un positivo en COVID-19.
 * Contiene tanto las localizaciones registradas por el dispositivo del usuario que
 * ha notificado ser positivo, como las fechas correspondientes a dichas localizaciones.
 * Opcionalmente, puede contener los datos personales del usuario.
 */
data class Positive(
        val locations: List<UserLocation>, // Localizaciones registradas
        val locationDates: List<String>, // Lista de fechas correspondientes a las localizaciones
        val personalData: PersonalData? // Datos personales (Opcional)
)
