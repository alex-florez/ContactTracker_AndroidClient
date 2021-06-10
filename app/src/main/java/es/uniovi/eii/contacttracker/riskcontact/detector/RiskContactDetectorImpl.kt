package es.uniovi.eii.contacttracker.riskcontact.detector

import android.util.Log
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.model.Itinerary
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.util.LocationUtils.toRadians
import es.uniovi.eii.contacttracker.util.Utils
import java.text.SimpleDateFormat
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
            var riskContact: RiskContact? = null // Contacto de riesgo
            if(pLocations != null) {  // Comprobar si hay localizaciones del positivo para esa fecha
                uLocations?.forEach { userLocation ->
                    /* Buscar el punto más cercano del positivo (y que no haya sido ya comprobado) */
                    val (closestLocation, distance) = findClosestLocation(userLocation, pLocations)
                    if(closestLocation != null){
                        /* Comprobar Cercanía en el Espacio y en el tiempo */
                        if(checkSpaceProximity(userLocation, closestLocation, 5.0) // Cercanía en el ESPACIO
                            && checkTimeProximity(userLocation, closestLocation, 5.0) // Cercanía en el TIEMPO
                        ) {
                            if(riskContact == null){ /* Registrar nuevo contacto */
                                riskContact = RiskContact() // Iniciar nuevo Tramo de contacto.
                            }
                            /* Actualizar Contacto de Riesgo si ya se estaba en un tramo de contacto */
                            riskContact?.addContactLocations(userLocation, closestLocation)
                            usedLocations.add(closestLocation) // Marcar la localización del positivo como usada
                        } else {
                            riskContact?.let{
                                /* Almacenar el Contacto de Riesgo en el resultado.*/
                                result.riskContacts.add(it)
                                /* Cerrar Tramo de Contacto de Riesgo (si había uno existente) */
                                riskContact = null
                            }
                        }
                    }
                }
            }
        }

//        val riskContact = RiskContact()
//        val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
//        val t1 = df.parse("08/06/2021 12:10:00")
//        val t2 = df.parse("08/06/2021 12:15:00")
//        val t3 = df.parse("08/06/2021 12:18:32")
//
//        val t4 = df.parse("08/06/2021 12:09:00")
//        val t5 = df.parse("08/06/2021 12:12:00")
//        val t6 = df.parse("08/06/2021 12:20:00")
//
//
//        val p1 = UserLocation(null, 43.537642474258135, -5.903910236700368, 20.0, "provider", t1!!)
//        val p2 = UserLocation(null, 43.53766143207932, -5.903800936691187, 20.0, "provider", t2!!)
//        val p3 = UserLocation(null, 43.537725596968286, -5.903757350797957, 20.0, "provider", t3!!)
//
//        val g1 = UserLocation(null, 43.53762636227226, -5.903901836502487, 20.0, "provider", t4!!)
//        val g2 = UserLocation(null, 43.53765309766653, -5.903778454897031, 20.0, "provider", t5!!)
//        val g3 = UserLocation(null, 43.53771580427199, -5.903716764094304, 20.0, "provider", t6!!)
//
//        riskContact.addContactLocations(p1, g1)
//        riskContact.addContactLocations(p2, g2)
//        riskContact.addContactLocations(p3, g3)
//
//        val meanTime = Utils.getMinuteSecond(riskContact.meanTimeInterval)
//        Log.d("MEANTIME", "${meanTime[0]} min ${meanTime[1]} secs")




//        val t3 = df.parse("08/06/2021 12:08:00")
//        val t4 = df.parse("08/06/2021 12:17:00")
//        val rc = RiskContact()
//        val (inf, sup) = rc.getIntersection(t1, t2, t3!!, t4!!)
//        val (closest, distance) = findClosestLocation(p1, listOf(p2,p3,p4))
//        closest?.let {
//            Log.d("más cercano", "Más cercano: $it distancia: $distance")
//        }

//        Log.d("INTERSECT", "Inferior: $inf Superior: $sup")
        return result
    }



    override fun findClosestLocation(
        location: UserLocation,
        otherLocations: List<UserLocation>
    ): Pair<UserLocation?, Double> {
        var closest: UserLocation? = null
        var minimumDistance = Double.POSITIVE_INFINITY
        otherLocations.forEach { other ->
            val distance = LocationUtils.distance(location, other)
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
        val minDiff = Utils.dateDifferenceInSecs(pointA.locationTimestamp, pointB.locationTimestamp) / 60
        return minDiff <= time
    }

    override fun checkSpaceProximity(
        pointA: UserLocation,
        pointB: UserLocation,
        radius: Double
    ): Boolean {
        return LocationUtils.distance(pointA, pointB) <= radius
    }


}