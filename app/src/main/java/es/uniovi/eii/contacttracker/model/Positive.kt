package es.uniovi.eii.contacttracker.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.util.Utils
import java.util.Date

/**
 * Clase que representa a un positivo en COVID-19.
 * Contiene tanto las localizaciones registradas por el dispositivo del usuario que
 * ha notificado ser positivo, como las fechas correspondientes a dichas localizaciones.
 * Opcionalmente, puede contener los datos personales del usuario.
 */
class Positive(

        @Expose
        @SerializedName("id")
        val id: String?, // ID autogenerado por Firestore

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
) {
        /**
         * Construye un objeto Itinerary a partir de la
         * lista de localizaciones registradas y de la lista
         * de fechas.
         *
         * @return Itinerario con las localizaciones organizadas por fechas.
         */
//        fun getItinerary(): Itinerary {
//                val map: MutableMap<String, List<UserLocation>> = mutableMapOf()
//                locationDates.forEach{ date ->
//                        val filteredLocations = locations.filter { l -> Utils.formatDate(l.timestamp(), "yyyy-MM-dd") == date }
//                        map[date] = filteredLocations
//                }
//                return Itinerary(map)
//        }
}
