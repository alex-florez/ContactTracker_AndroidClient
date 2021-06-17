package es.uniovi.eii.contacttracker.riskcontact.detector

import es.uniovi.eii.contacttracker.model.*

/**
 * Interfaz que representa la funcionalidad del detector
 * de contactos de riesgo. Contiene la definición del algoritmo
 * utilizado para comparar las localizaciones.
 */
interface RiskContactDetector {

    /**
     * Ejecuta la comprobación comparando el itinerario del usuario
     * del cliente Android con el itinerario del positivo. Devuelve una lista
     * con todos los tramos de contacto de riesgo que se hayan detectado.
     *
     * @param user Itinerario del propio usuario.
     * @param positive Itinerario del positivo.
     * @return Lista con los contactos de riesgo o vacía si no hay ninguno.
     */
    fun startChecking(user: Itinerary, positive: Itinerary): List<RiskContact>

    /**
     * CERCANÍA EN EL ESPACIO
     *
     * Calcula la distancia entre los dos puntos localizados en la Tierra con
     * su latitud y longitud, y compara dicha distancia aproximada con el radio
     * pasado como parámetro para determinar si el punto B está dentro del círculo
     * delimitado por el punto A con el radio pasado como parámetro.
     *
     * @param pointA Centro del círculo marcado con Latitud y Longitud.
     * @param pointB Punto del mapa marcado con Latitud y Longitud.
     * @param radius Radio del círculo en METROS.
     * @return True si el punto B está dentro del círculo delimitado por el punto A y el radio indicado.
     */
    fun checkSpaceProximity(pointA: UserLocation, pointB: UserLocation, radius: Double): Boolean

    /**
     * Devuelve la localización más próxima a la localización tomada como referencia.
     * @param location Localización de referencia.
     * @param otherLocations Localizaciones a comprobar.
     * @return Tupla con la localización más cercana a la localización de referencia y
     * la distancia correspondiente.
     */
    fun findClosestLocation(location: UserLocation, otherLocations: List<UserLocation>): Pair<UserLocation?, Double>

    /**
     * CERCANÍA EN EL TIEMPO
     *
     * Calcula la diferencia de tiempo entre las dos localizaciones registradas pasadas como
     * parámetro. Si dicha diferencia es menor o igual que el margen de tiempo indicado, entonces
     * se considera que las localizaciones están próximas en el tiempo.
     *
     * @param pointA Localización A.
     * @param pointB Localización B.
     * @param time Margen de tiempo en MINUTOS con el que se hace la comparación.
     * @return True si la diferencia temporal es menor o igual que el margen de tiempo especificado.
     */
    fun checkTimeProximity(pointA: UserLocation, pointB: UserLocation, time:Double): Boolean

    /**
     * Establece la configuración de la comprobación con la nueva
     * configuración pasada como parámetro.
     *
     * @param config Configuración de la comprobación.
     */
    fun setConfig(config: RiskContactConfig)
}