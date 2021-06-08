package es.uniovi.eii.contacttracker.model

import android.os.CpuUsageInfo
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.util.Utils
import java.util.Date
import kotlin.math.abs

/**
 * Constantes.
 */
private const val USER = "user"
private const val POSITIVE = "positive"

/**
 * Clase de datos que contiene la información de un
 * contacto de riesgo con un positivo.
 */
class RiskContact {

    /**
     * Mapa que representa el TRAMO de localizaciones en los que
     * tuvo lugar el contacto. Contiene tanto las localizaciones del
     * positivo como las del usuario que hace la comprobación.
     */
    val contactLocations: MutableMap<String, MutableList<UserLocation>> =
        mutableMapOf(USER to mutableListOf(), POSITIVE to mutableListOf())

    /**
     * Nivel de Riesgo del Contacto, basado en el número
     * de localizaciones de contacto, tiempo de exposición y
     * proximidad media.
     */
    var riskLevel: Pair<RiskLevel, Double> = Pair(RiskLevel.VERDE, 0.0)

    /**
     * Tiempo de exposición total en formato array con
     * minutos y segundos [minutes, secs]
     */
    var exposeTime: Array<Int> = arrayOf(0, 0)

    /**
     * Proximidad media.
     */
    var meanProximity: Double = 0.0

    /**
     * Añade el par de localizaciones correspondiente al usuario del móvil
     * y al positivo, los cuales han estado en contacto. Una vez añadidos, se
     * re-calculan las propiedades del contacto de riesgo.
     *
     * @param userLocation Localización del propio usuario.
     * @param positiveLocation Localización del positivo.
     */
    fun addContactLocations(userLocation: UserLocation, positiveLocation: UserLocation) {
        contactLocations[USER]?.add(userLocation)
        contactLocations[POSITIVE]?.add(positiveLocation)
        calculateExposeTime() // Recalcular tiempo de exposición.
        calculateMeanProximity() // Recalcular proximidad media.
        calculateRiskLevel() // Recalcular nivel de riesgo.
    }

    /**
     * Calcula el tiempo total de exposición.
     */
    fun calculateExposeTime() {
        contactLocations[USER]?.let{ userLocs ->
            contactLocations[POSITIVE]?.let { positiveLocs ->
                if(userLocs.size > 0 && positiveLocs.size > 0){
                    // Primeras localizaciones
                    val firstUserLocation = userLocs[0]
                    val firstPositiveLocation = positiveLocs[0]
                    // Últimas localizaciones
                    val lastUserLocation = userLocs[userLocs.size-1]
                    val lastPositiveLocation = positiveLocs[positiveLocs.size-1]
                    // Calcular el límite superior en inferior de la intersección.
                    val (inferior, superior) = getIntersection(firstUserLocation.locationTimestamp, lastUserLocation.locationTimestamp,
                        firstPositiveLocation.locationTimestamp, lastPositiveLocation.locationTimestamp)
                    if(inferior != null && superior != null) {
                        val diff = abs(inferior.time - superior.time) // Diferencia de tiempo
                        exposeTime = Utils.getMinuteSecond(diff) // Formatear a [min, sec]
                    }
                }
            }
        }
    }

    /**
     * Calcula y devuelve el par de fechas que definen la intersección entre cuatro
     * fechas que se corresponden a cuatro localizaciones, 2 de un positivo, y 2 del
     * propio usuario.
     */
    private fun getIntersection(date11: Date, date12: Date, date21: Date, date22: Date): Pair<Date?, Date?>{
        // Casos en los que no hay intersección.
        if(date22.before(date11) || date21.after(date12) || date22 == date11 || date21 == date12){
            return Pair(null, null)
        }
        // Límite superior
        val superior: Date = if(date22.after(date12)) date12 else  date22
        // Límite inferior
        val inferior: Date = if(date21.before(date11)) date11 else date21
        return Pair(inferior, superior)
    }

    /**
     * Calcula la media de proximidad entre las localizaciones
     * del usuario y del positivo.
     */
    fun calculateMeanProximity() {
        var proximity = 0.0
        contactLocations[USER]?.let{ userLocs ->
            contactLocations[POSITIVE]?.let { positiveLocs ->
                var i = 0
                userLocs.forEach { userLocation ->
                    val positiveLocation = positiveLocs[i]
                    proximity += LocationUtils.distance(userLocation, positiveLocation)
                    i++
                }
                if(i > 0)
                    meanProximity = proximity / i
            }
        }
    }

    /**
     * Calcula el nivel de riesgo.
     */
    fun calculateRiskLevel() {

    }
}
