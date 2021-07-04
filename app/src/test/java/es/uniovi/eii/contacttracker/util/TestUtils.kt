package es.uniovi.eii.contacttracker.util

import es.uniovi.eii.contacttracker.model.Itinerary
import es.uniovi.eii.contacttracker.model.Point
import es.uniovi.eii.contacttracker.model.UserLocation
import java.text.SimpleDateFormat

/* Date Formatter */
private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

/* Funciones de Utilidad para los Tests Unitarios */
object TestUtils {
    /**
     * Lee las líneas del fichero de nombre indicado y
     * devuelve una lista de Strings con cada una de las líneas.
     *
     * @param filename Nombre del fichero.
     * @return Lista con las líneas del fichero.
     */
    fun readFile(filename: String): List<String>{
        var list = listOf<String>()
        javaClass.classLoader?.getResourceAsStream(filename)?.bufferedReader()
            .use {
                if(it != null)
                    list = it.readLines()
            }
        return list
    }

    /**
     * Lee los datos del fichero de nombre indicado y
     * construye un Itinerario con las localizaciones.
     *
     * @param filename Nombre del fichero.
     * @return Objeto Itinerary.
     */
    fun parseItinerary(filename: String): Itinerary {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        val lines = readFile(filename)
        val locations = mutableListOf<UserLocation>()
        var count = 1
        lines.forEach {
            val data = it.split(",")
            val lng = data[0].toDouble()
            val lat = data[1].toDouble()
            val date = df.parse(data[2])
            locations.add(UserLocation(count.toLong(), Point(lat, lng, date!!), 0.0,""))
            count++
        }
        // Crear itinerario
        return Itinerary(locations)
    }
}

