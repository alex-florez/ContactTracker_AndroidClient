package es.uniovi.eii.contacttracker.model

import es.uniovi.eii.contacttracker.util.DateUtils

/* Formato de las fechas */
const val dateFormat = "yyyy-MM-dd"

/**
 * Clase que representa un itinerario formado
 * por localizaciones agrupadas por días en las
 * que fueron registradas.
 */
class Itinerary(
        locations: List<UserLocation>
) {

        /* Mapa con las localizaciones clasificadas por fecha */
        private var groupedLocations: MutableMap<String, List<UserLocation>> = mutableMapOf()

        init {
                /* Agrupar localizaciones por días */
                val dates = locations.map {
                        DateUtils.formatDate(it.timestamp(), dateFormat)
                }.distinct()
                dates.forEach { date ->
                        groupedLocations[date] = locations.filter {
                                DateUtils.formatDate(it.timestamp(), dateFormat) == date
                        }
                }
        }


        /**
         * Devuelve la lista de fechas formateadas a string que se
         * corresponden con las claves del mapa de localizaciones.
         */
        fun dates(): List<String> = groupedLocations.keys.toList()

        /**
         * Operador de indexación personalizado para acceder a las localizaciones
         * asociadas a una fecha cuyo string es pasado como parámetro.
         *
         * @param date Fecha formateada a string (yyyy-MM-dd).
         * @return lista de localizaciones asociadas a esa fecha.
         */
        operator fun get(date: String): List<UserLocation> = groupedLocations[date] ?: listOf()
}
