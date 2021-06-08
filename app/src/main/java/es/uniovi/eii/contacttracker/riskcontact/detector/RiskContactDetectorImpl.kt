package es.uniovi.eii.contacttracker.riskcontact.detector

import android.util.Log
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.model.Itinerary
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.util.LocationUtils.toRadians
import java.util.Date
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Clase que implementa la interfaz que define el Detector de
 * Contactos de Riesgo y que contiene el algoritmo para hacer
 * las comprobaciones.
 */
class RiskContactDetectorImpl : RiskContactDetector {

    /**
     * Almacena las localizaciones que ya han sido utilizadas para comprobar.
     */
    private val usedLocations = mutableListOf<UserLocation>()

    override fun startChecking(user: Itinerary, positive: Itinerary): RiskContactResult {
        val result = RiskContactResult()
        usedLocations.clear()

        /* Recorrer localizaciones del itinerario del usuario por cada día */
        user.locations.keys.forEach { date ->
            val uLocations = user.locations[date] // Localizaciones del usuario
            val pLocations = positive.locations[date] // Localizaciones del positivo
            if(pLocations != null) {  // Comprobar si hay localizaciones del positivo para esa fecha
                uLocations?.forEach { userLocation ->
                    var riskContact: RiskContact? = null // Contacto de riesgo

                    /* Buscar el punto más cercano del positivo (y que no haya sido ya comprobado) */
                    val (closestLocation, distance) = findClosestLocation(userLocation, pLocations)
                    if(closestLocation != null){
                        /* Comprobar Cercanía en el Espacio y en el tiempo */
                        if(checkSpaceProximity(userLocation, closestLocation, 5.0) // Cercanía en el ESPACIO
                            && checkTimeProximity(userLocation, closestLocation, 5.0) // Cercanía en el TIEMPO
                        ) {
                            if(riskContact == null){ /* Registrar nuevo contacto */
                                // Iniciar nuevo Tramo de contacto.
                                riskContact = RiskContact()
                            } else { /* Actualizar Contacto de Riesgo si ya se estaba en un tramo de contacto */

                            }
                            usedLocations.add(closestLocation) // Marcar la localización del positivo como usada
                        } else {
                            /* Cerrar Tramo de Contacto de Riesgo (si había uno existente) */
                            /* Almacenar el Contacto de Riesgo en el resultado.*/
                        }
                    }
                }
            }
        }


        val p1 = UserLocation(null, 43.53763611044242, -5.903921658857786, 20.0, "provider", Date())
        val p2 = UserLocation(null, 43.5377158304792, -5.903756703015708, 20.0, "provider", Date())
        val p3 = UserLocation(null, 43.53765555436356, -5.903804312222162, 20.0, "provider", Date())
        val p4 = UserLocation(null, 43.53784075739798, -5.904127518384281, 20.0, "provider", Date())

        val (closest, distance) = findClosestLocation(p1, listOf(p2,p3,p4))
        closest?.let {
            Log.d("más cercano", "Más cercano: $it distancia: $distance")
        }
        return RiskContactResult()
    }

    override fun checkSpaceProximity(
        pointA: UserLocation,
        pointB: UserLocation,
        radius: Double
    ): Boolean {
      return false
    }

    override fun findClosestLocation(
        location: UserLocation,
        otherLocations: List<UserLocation>
    ): Pair<UserLocation?, Double> {
        var closest: UserLocation? = null
        var minimumDistance = Double.POSITIVE_INFINITY
        otherLocations.forEach { other ->
            val distance = distance(location, other)
            if(distance < minimumDistance  // Menor distancia
                && !usedLocations.contains(other) // Localización no ha sido ya utilizada
            ) {
                // Nuevo mínimo
                minimumDistance = distance
                closest = other
            }
        }
        return Pair(closest, minimumDistance)
    }

    override fun checkTimeProximity(
        pointA: UserLocation,
        pointB: UserLocation,
        time: Double
    ): Boolean {
       return false
    }

    override fun distance(pointA: UserLocation, pointB: UserLocation): Double {
        /* Haversin Formula */
        /* Considera que la tierra es una ESFERA y determina la GREAT-CIRCLE Distance */
        // Grados de diferencia entre latitudes y longitudes
        val latDiff = pointB.lat - pointA.lat
        val lngDiff = pointB.lng - pointA.lng
        // Convertir a radianes los grados de diferencia entre las latitudes y longitudes.
        val latDiffRads = toRadians(latDiff)
        val lngDiffRads = toRadians(lngDiff)
        // Calcular el término 'a' que está dentro de la raíz.
        val a = sin(latDiffRads/2) * sin(latDiffRads/2) +
                cos(toRadians(pointA.lat)) * cos(toRadians(pointB.lat)) *
                sin(lngDiffRads/2) * sin(lngDiffRads/2)
        // Aplicar arcotangente de dos parámetros y raíz cuadrada
        // Multiplicar por el radio de la tierra para obtener los km de distancia.
        val d = 2 * atan2(sqrt(a), sqrt(1-a)) * Constants.EARTH_RADIUS
        // Devolver distancia en metros y con 4 decimales
        return "%.4f".format(d * 1000).toDouble()
    }


}