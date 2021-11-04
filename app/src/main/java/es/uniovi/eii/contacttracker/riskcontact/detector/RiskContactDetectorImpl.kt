package es.uniovi.eii.contacttracker.riskcontact.detector

import es.uniovi.eii.contacttracker.model.*
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.util.DateUtils
import javax.inject.Inject

/**
 * Clase que implementa la interfaz que define el Detector de
 * Contactos de Riesgo y que contiene el algoritmo para hacer
 * las comprobaciones entre las localizaciones de un positivo y de un usuario.
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
        user.dates().forEach { date ->
            val uLocations = user[date] // Localizaciones del usuario
            val pLocations = positive[date] // Localizaciones del positivo
            var riskContact: RiskContact? = null // Contacto de riesgo actual
            // Comprobar si hay localizaciones del positivo para esa fecha
            uLocations.forEach { userLocation ->
                /* Buscar el punto más cercano del positivo (y que no haya sido ya comprobado) */
                val (closestLocation) = findClosestLocation(userLocation, pLocations)
                /* Comprobar Cercanía en el Espacio y en el tiempo */
                if(closestLocation != null && checkProximity(userLocation, closestLocation,
                        riskContactConfig.timeDifferenceMargin, riskContactConfig.securityDistanceMargin)) {
                    /* Iniciar nuevo tramo de contacto si aún no hay ninguno abierto. */
                    if(riskContact == null){
                        riskContact = RiskContact(config = riskContactConfig, positiveLabel = positive.label) // Iniciar nuevo Tramo de contacto.
                    }
                    /* Actualizar Contacto de Riesgo */
                    riskContact?.let { update(it,userLocation, closestLocation) }
                } else {
                    /* Comprobar si existe un contacto de riesgo abierto */
                    riskContact?.let {
                        result.add(it)     /* Almacenar el Contacto de Riesgo en el resultado. */
                        riskContact = null  /* Cerrar Tramo de Contacto de Riesgo (si había uno existente) */
                    }
                }
            }
            /* Comprobar si existe un contacto de riesgo abierto */
            riskContact?.let {
                result.add(it)     /* Almacenar el Contacto de Riesgo en el resultado. */
                riskContact = null  /* Cerrar Tramo de Contacto de Riesgo (si había uno existente) */
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
            val distance = LocationUtils.distance(location.point, other.point)
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
        time: Int
    ): Boolean {
        val secsDiff = DateUtils.dateDifferenceInSecs(pointA.timestamp(), pointB.timestamp())
        return secsDiff <= time
    }

    override fun setConfig(config: RiskContactConfig) {
        this.riskContactConfig = config
    }

    override fun checkSpaceProximity(
        pointA: UserLocation,
        pointB: UserLocation,
        radius: Double
    ): Boolean {
        return LocationUtils.distance(pointA.point, pointB.point) <= radius
    }

    /**
     * Método privado auxiliar para comprobar la cercanía tanto en el espacio
     * como en el tiempo entre las dos localizaciones pasadas como parámetro.
     *
     * @param pointA Localización A.
     * @param pointB Localización B.
     * @param time Margen temporal para comparar la cercanía en el tiempo.
     * @param radius Distancia para comprar la cercanía en el espacio.
     * @return True si existe cercanía en el espacio y en el tiempo.
     */
    private fun checkProximity(
        pointA: UserLocation,
        pointB: UserLocation,
        time: Int,
        radius: Double
    ): Boolean {
        return checkSpaceProximity(pointA, pointB, radius) // Cercanía en el ESPACIO
                && checkTimeProximity(pointA, pointB, time) // Cercanía en el TIEMPO
    }


    /**
     * Actualiza el contacto de riesgo existente añadiéndole las localizaciones
     * del usuario y del positivo, las cuales coinciden en el tiempo y en el espacio.
     *
     * @param riskContact Contacto de riesgo a actualizar.
     * @param userLocation Localización del usuario.
     * @param positiveLocation Localización del positivo.
     */
    private fun update(riskContact: RiskContact,
                       userLocation: UserLocation,
                       positiveLocation: UserLocation) {
        /* Actualizar Contacto de Riesgo con las localizaciones del usuario y del positivo. */
        riskContact.addContactLocations(userLocation, positiveLocation)
        usedLocations.add(positiveLocation) // Marcar la localización del positivo como usada
    }
}