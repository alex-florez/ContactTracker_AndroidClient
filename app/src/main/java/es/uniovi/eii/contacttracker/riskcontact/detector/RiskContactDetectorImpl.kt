package es.uniovi.eii.contacttracker.riskcontact.detector

import es.uniovi.eii.contacttracker.model.*
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.util.Utils
import javax.inject.Inject

/* Formato estándar para las fechas utilizadas en el algoritmo de comprobación. */
private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

/**
 * Clase que implementa la interfaz que define el Detector de
 * Contactos de Riesgo y que contiene el algoritmo para hacer
 * las comprobaciones.
 */
class RiskContactDetectorImpl @Inject constructor() : RiskContactDetector {

    /**
     * Almacena las localizaciones que ya han sido utilizadas para comprobar.
     */
    private val usedLocations = mutableListOf<UserLocation>()

    /**
     * Configuración de la comprobación.
     */
    private var riskContactConfig: RiskContactConfig = RiskContactConfig()

    override fun startChecking(user: Itinerary, positive: Itinerary): List<RiskContact> {
        val result = mutableListOf<RiskContact>()
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
                                riskContact = RiskContact(config = riskContactConfig) // Iniciar nuevo Tramo de contacto.
                            }
                            /* Actualizar Contacto de Riesgo si ya se estaba en un tramo de contacto */
                            riskContact?.addContactLocations(userLocation, closestLocation)
                            usedLocations.add(closestLocation) // Marcar la localización del positivo como usada
                        } else {
                            riskContact?.let{
                                /* Almacenar el Contacto de Riesgo en el resultado.*/
                                result.add(it)
                                /* Cerrar Tramo de Contacto de Riesgo (si había uno existente) */
                                riskContact = null
                            }
                        }
                    }
                }
            }
        }
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
        val minDiff = Utils.dateDifferenceInSecs(pointA.locationTimestamp, pointB.locationTimestamp) / 60.0
        return minDiff <= time
    }

    override fun setConfig(config: RiskContactConfig) {
        this.riskContactConfig = config
    }

    override fun checkSpaceProximity(
        pointA: UserLocation,
        pointB: UserLocation,
        radius: Double
    ): Boolean {
        return LocationUtils.distance(pointA, pointB) <= radius
    }


}