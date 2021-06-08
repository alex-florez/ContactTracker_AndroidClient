package es.uniovi.eii.contacttracker.model

/**
 * Clase de datos que representa un itinerario formado
 * por localizaciones a lo largo de distintas fechas.
 */
class Itinerary(
        val locations: Map<String, List<UserLocation>>
) {

}
