package es.uniovi.eii.contacttracker.model

import android.os.CpuUsageInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
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
@Entity
class RiskContact {

    @PrimaryKey(autoGenerate = true)
    var riskContactId: Long? = null

    /* Foreign Key para el resultado. */
    var riskContactResultId: Long? = null

    /**
     * Lista de pares de localizaciones que representa el TRAMO de localizaciones en los que
     * tuvo lugar el contacto. Contiene tanto las localizaciones del positivo como las
     * del propio usuario que hace la comprobación.
     */
    @Ignore
    val contactLocations: MutableList<RiskContactLocation> = mutableListOf()

    /**
     * Nivel de Riesgo del Contacto, basado en el número
     * de localizaciones de contacto, tiempo de exposición y
     * proximidad media.
     */
    var riskLevel: RiskLevel = RiskLevel.VERDE

    /**
     * Porcentaje del Nivel de Riesgo del contacto.
     */
    var riskScore: Double = 0.0

    /**
     * Tiempo de exposición total en formato de milisegundos.
     */
    var exposeTime: Long = 0L

    /**
     * Proximidad media (metros).
     */
    var meanProximity: Double = 0.0

    /**
     * Intervalo de tiempo medio (milisegundos).
     */
    var meanTimeInterval: Long = 0L

    /**
     * Fecha de inicio del contacto. (Aproximada)
     */
    var startDate: Date = Date()

    /**
     * Fecha de fin de contacto. (Aproximada)
     */
    var endDate: Date = Date()

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
        calculateMeanTimeInterval() // Recalcular intervalo de tiempo medio.
        calculateRiskLevel() // Recalcular nivel de riesgo.
    }

    /**
     * Calcula el tiempo total de exposición.
     */
    fun calculateExposeTime() {
        if(contactLocations.size > 0){
            // Primeras localizaciones
            val firstUserLocation = contactLocations[0].userLocation
            val firstPositiveLocation = contactLocations[0].positiveLocation
            // Últimas localizaciones
            val lastUserLocation = userLocs[userLocs.size-1]
            val lastPositiveLocation = positiveLocs[positiveLocs.size-1]
            // Calcular el límite superior en inferior de la intersección.
            val (inferior, superior) = getIntersection(firstUserLocation.locationTimestamp, lastUserLocation.locationTimestamp,
                    firstPositiveLocation.locationTimestamp, lastPositiveLocation.locationTimestamp)
            if(inferior != null && superior != null) {
                exposeTime = abs(inferior.time - superior.time) // Diferencia de tiempo en milisegundos.
                // Asignar fechas de inicio y fin de contacto.
                startDate = inferior
                endDate = superior
            }
        }
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
                        exposeTime = abs(inferior.time - superior.time) // Diferencia de tiempo en milisegundos.
                        // Asignar fechas de inicio y fin de contacto.
                        startDate = inferior
                        endDate = superior
                    }
                }
            }
        }
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
     * Calcula el intervalo de tiempo medio entre localizaciones
     * de ambos individuos, tanto del positivo como del propio usuario.
     */
    fun calculateMeanTimeInterval() {
        var time = 0L
        var n = 0
        contactLocations[USER]?.let { userLocations ->
            contactLocations[POSITIVE]?.let {positiveLocations ->
                var i = 0
                userLocations.forEach { userLocation ->
                    // Si no es la última localización
                    if(i < userLocations.size - 1){
                        val positiveLocation = positiveLocations[i]
                        val nextPositiveLocation = positiveLocations[i+1]
                        val nextUserLocation = userLocations[i+1]
                        // Intervalo de tiempo del positivo
                        time += abs(positiveLocation.locationTimestamp.time
                                - nextPositiveLocation.locationTimestamp.time)
                        // Intervalo de tiempo del usuario
                        time += abs(userLocation.locationTimestamp.time
                                - nextUserLocation.locationTimestamp.time)
                        n += 2 // Se han analizado dos intervalos
                        i++
                    }
                }
                if(n > 0)
                    meanTimeInterval = time / n // Calcular la media
            }
        }
    }

    /**
     * Calcula el nivel de riesgo en función de los parámetros
     * del contacto de riesgo y de los factores de ponderación establecidos.
     */
    fun calculateRiskLevel() {
        var riskScore = 0.0 // Valor total ponderado del riesgo.
        /* Normalizar los valores de los parámetros */
        val (exposeTimeNormal, meanProximityNormal, meanTimeIntervalNormal) = normalize()
        /* Obtener valores ponderados de los parámetros */
        riskScore += exposeTimeNormal * 0.3 + meanProximityNormal * 0.5 + meanTimeIntervalNormal * 0.2
        val riskPercent: Double = riskScore * 100 // Porcentaje total de riesgo.
        riskLevel = getRiskLevel(riskPercent)
        this.riskScore = "%.2f".format(riskPercent).toDouble()
    }

    /**
     * Normaliza los valores de los parámetros del contacto de
     * riesgo para trasladarlos a un intervalo de [0,1].
     *
     * @return Tripleta con los tres parámetros normalizados.
     */
    private fun normalize(): Triple<Double, Double, Double> {
        // Tiempo de exposición
        val exposeTimeNormal = (exposeTime - 0)/(900000.0 - 0) // Max: 15 min
        // Proximidad media
        val meanProximityNormal = (meanProximity - 0) / (10.0-0) // Máx: 10 m
        // Intervalo de tiempo medio
        val meanTimeIntervalNormal = (meanTimeInterval - 0) / (600000.0 - 0) // Max: 10 min
        return Triple(exposeTimeNormal, meanProximityNormal, meanTimeIntervalNormal)
    }

    /**
     * Devuelve el nivel de riesgo correspondiente en función
     * del porcentaje numérico del riesgo pasado como parámetro.
     *
     * @param riskPercent Porcentaje de riesgo.
     * @return Nivel de riesgo asociado.
     */
    private fun getRiskLevel(riskPercent: Double): RiskLevel {
        return if(riskPercent <= 25) RiskLevel.AMARILLO
        else if(riskPercent <= 75) RiskLevel.NARANJA
        else RiskLevel.ROJO
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
}
