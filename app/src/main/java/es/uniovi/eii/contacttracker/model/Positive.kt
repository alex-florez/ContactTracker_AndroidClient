package es.uniovi.eii.contacttracker.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import es.uniovi.eii.contacttracker.model.UserLocation
import java.util.Date

/**
 * Clase de datos que representa a un positivo en COVID-19.
 * Contiene tanto las localizaciones registradas por el dispositivo del usuario que
 * ha notificado ser positivo, como las fechas correspondientes a dichas localizaciones.
 * Opcionalmente, puede contener los datos personales del usuario.
 */
data class Positive(

        @Expose
        @SerializedName("timestamp")
        val timestamp: Date, // Fecha de notificación del positivo

        @Expose
        @SerializedName("locations")
        val locations: List<UserLocation>, // Localizaciones registradas

        @Expose
        @SerializedName("locationDates")
        val locationDates: List<String>, // Lista de fechas correspondientes a las localizaciones

        @Expose
        @SerializedName("personalData")
        val personalData: PersonalData? // Datos personales (Opcional)
)
