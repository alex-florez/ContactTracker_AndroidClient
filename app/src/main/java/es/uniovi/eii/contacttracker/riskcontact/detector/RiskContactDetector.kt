package es.uniovi.eii.contacttracker.riskcontact.detector

import es.uniovi.eii.contacttracker.model.Itinerary
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.model.UserLocation

/**
 * Interfaz que representa la funcionalidad del detector
 * de contactos de riesgo. Contiene la definición del algoritmo
 * utilizado para comparar las localizaciones.
 */
interface RiskContactDetector {

    /**
     * Ejecuta la comprobación comparando el itinerario del usuario
     * del cliente Android con el itinerario del positivo. Devuelve un objeto
     * resultado con los datos e información relativa a la comprobación.
     *
     * @param user Itinerario del propio usuario.
     * @param positive Itinerario del positivo.
     * @return Objeto con los resultados de la comprobación.
     */
    fun startChecking(user: Itinerary, positive: Itinerary): RiskContactResult

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
     * Utiliza alguno de los algoritmos típicos para calcular distancias entre
     * dos puntos en una figura esférica similar a la tierra (Haversin Formula, Vincenty...)
     * y devuelve la distancia aproximada en metros.
     * @param pointA Localización A con latitud y longitud.
     * @param pointB Localización B con latitud y longitud.
     * @return Distancia entre los puntos medida en METROS.
     */
    fun distance(pointA: UserLocation, pointB: UserLocation): Double
}