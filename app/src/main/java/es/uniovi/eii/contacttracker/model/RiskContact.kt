package es.uniovi.eii.contacttracker.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import es.uniovi.eii.contacttracker.adapters.riskcontact.RiskContactDiffCallback
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.util.NumberUtils.round
import kotlinx.parcelize.Parcelize
import java.util.Date
import kotlin.math.abs

/**
 * Clase de datos que contiene la información de un
 * contacto de riesgo con un positivo.
 */
@Parcelize
@Entity(tableName = "risk_contacts")
class RiskContact(
        @PrimaryKey(autoGenerate = true) var riskContactId: Long? = null,
        var riskContactResultId: Long? = null, /* Foreign Key para el resultado */
        @Ignore var contactLocations: MutableList<RiskContactLocation> = mutableListOf(), /* lista de Pares de localizaciones que representa el TRAMO de localizaciones en los que
                                                                                         tuvo lugar el contacto. Contiene tanto las localizaciones del positivo como las
                                                                                         el propio usuario que hace la comprobación.*/
        var riskLevel: RiskLevel = RiskLevel.VERDE, /* Nivel de Riesgo del Contacto, basado en el número de
                                                        localizaciones de contacto, tiempo de exposición y proximidad media.*/
        var riskPercent: Double = 0.0, /* Porcentaje de Nivel de Riesgo del Contacto */
        var riskScore: Double = 0.0, /* Nivel total de riesgo */
        var exposeTime: Long = 0L, /* Tiempo de exposición total en formato de milisegundos */
        var meanProximity: Double = 0.0, /* Proximidad media (metros) */
        var meanTimeInterval: Long = 0L, /* Intervalo de tiempo medio (milisegundos) */

        var startDate: Date? = null, /* Fecha de inicio del contacto. (Aproximada) */
        var endDate: Date? = null, /* Fecha de fin de contacto. (Aproximada) */
        var positiveLabel: String = "", /* Etiqueta del positivo con el que se tuvo el contacto de riesgo. */
        @Ignore var config: RiskContactConfig = RiskContactConfig() /* Configuración de la comprobación */
) : Parcelable {

    /**
     * Añade el par de localizaciones correspondiente al usuario del móvil
     * y al positivo, los cuales han estado en contacto. Una vez añadidos, se
     * re-calculan las propiedades del contacto de riesgo.
     *
     * @param userLocation Localización del propio usuario.
     * @param positiveLocation Localización del positivo.
     */
    fun addContactLocations(userLocation: UserLocation, positiveLocation: UserLocation) {
        contactLocations.add(RiskContactLocation(
                userContactPointName = userLocation.userlocationID.toString(),
                userContactPoint = Point(userLocation.lat(), userLocation.lng(), userLocation.timestamp()),
                positiveContactPointName = positiveLocation.userlocationID.toString(),
                positiveContactPoint = Point(positiveLocation.lat(), positiveLocation.lng(), positiveLocation.timestamp())
        ))
        calculateExposeTime() // Recalcular tiempo de exposición.
        calculateMeanProximity() // Recalcular proximidad media.
        calculateMeanTimeInterval() // Recalcular intervalo de tiempo medio.
        calculateRiskLevel() // Recalcular nivel de riesgo.
    }

    /**
     * Calcula el tiempo total de exposición.
     */
    private fun calculateExposeTime() {
        if(contactLocations.size > 0){
            // Primeras localizaciones
            val firstUserLocation = contactLocations[0].userContactPoint
            val firstPositiveLocation = contactLocations[0].positiveContactPoint
            // Últimas localizaciones
            val lastUserLocation = contactLocations[contactLocations.size-1].userContactPoint
            val lastPositiveLocation = contactLocations[contactLocations.size-1].positiveContactPoint
            // Calcular el límite superior en inferior de la intersección.
            val (inferior, superior) = getIntersection(firstUserLocation.timestamp, lastUserLocation.timestamp,
                    firstPositiveLocation.timestamp, lastPositiveLocation.timestamp)
            // Asignar fechas de inicio y fin de contacto.
            startDate = inferior
            endDate = superior
            if(inferior != null && superior != null) {
                exposeTime = abs(inferior.time - superior.time) // Diferencia de tiempo en milisegundos.
            }
        }
    }

    /**
     * Calcula la media de proximidad entre las localizaciones
     * del usuario y del positivo.
     */
    private fun calculateMeanProximity() {
        var proximity = 0.0
        contactLocations.forEach { contactLocation ->
            proximity += LocationUtils.distance(
                contactLocation.userContactPoint, contactLocation.positiveContactPoint)
        }
        if(contactLocations.isNotEmpty()){
            meanProximity = round(proximity / contactLocations.size, 4) // Redondear con 4 decimales
        }
    }

    /**
     * Calcula el intervalo de tiempo medio entre localizaciones
     * de ambos individuos, tanto del positivo como del propio usuario.
     */
    private fun calculateMeanTimeInterval() {
        var time = 0L
        var n = 0
        var i = 0 // Índice
        contactLocations.forEach { contact ->
            // Si no es la última localización
            if(i < contactLocations.size - 1){
                val next = contactLocations[i+1]
                val ownLocation = contact.userContactPoint
                val nextOwnLocation = next.userContactPoint
                val positiveLocation = contact.positiveContactPoint
                val nextPositiveLocation = next.positiveContactPoint
                // Intervalo de tiempo del positivo
                time += abs(positiveLocation.timestamp.time
                        - nextPositiveLocation.timestamp.time)
                // Intervalo de tiempo del usuario
                time += abs(ownLocation.timestamp.time
                        - nextOwnLocation.timestamp.time)
                n += 2 // Se han analizado dos intervalos
                i++
            }
        }
        if(n > 0)
            meanTimeInterval = time / n // Calcular la media
    }

    /**
     * Calcula el nivel de riesgo en función de los parámetros
     * del contacto de riesgo y de los factores de ponderación establecidos.
     */
    private fun calculateRiskLevel() {
        this.riskScore = 0.0 // Valor total ponderado del riesgo.
        /* Normalizar los valores de los parámetros */
        var (exposeTimeNormal, meanProximityNormal, meanTimeIntervalNormal) = normalize()
        /* Obtener valores ponderados de los parámetros */
        // Si la proximidad es 0 o el intervalo de tiempo es 0, establecerlos a 1 para que no influyan en la ponderación.
        meanProximityNormal = if(meanProximityNormal == 0.0) 1.0 else meanProximityNormal
        meanTimeIntervalNormal = if(meanTimeIntervalNormal == 0.0) 1.0 else meanTimeIntervalNormal
        // Restar -1 para los parámetros que ponderan inversamente.
        this.riskScore += exposeTimeNormal * config.exposeTimeWeight +
                (1 - meanProximityNormal) * config.meanProximityWeight +
                (1 - meanTimeIntervalNormal) * config.meanTimeIntervalWeight
        val riskPercent: Double = this.riskScore * 100 // Porcentaje total de riesgo.
        this.riskLevel = getRiskLevel(riskPercent)
        this.riskPercent = round(riskPercent, 2)
    }

    /**
     * Normaliza los valores de los parámetros del contacto de
     * riesgo para trasladarlos a un intervalo de [0,1].
     *
     * @return Tripleta con los tres parámetros normalizados.
     */
    private fun normalize(): Triple<Double, Double, Double> {
        // Tiempo de exposición
        val exposeTimeNormal = normalize(exposeTime.toDouble(),
                config.exposeTimeRange[0].toDouble(),
                config.exposeTimeRange[1].toDouble())
        val meanProximityNormal = normalize(meanProximity,
                config.meanProximityRange[0],
                config.meanProximityRange[1])
        val meanTimeIntervalNormal = normalize(meanTimeInterval.toDouble(),
                config.meanTimeIntervalRange[0].toDouble(),
                config.meanTimeIntervalRange[1].toDouble())
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
        return when {
            riskPercent <= 25 -> RiskLevel.AMARILLO
            riskPercent <= 75 -> RiskLevel.NARANJA
            else -> RiskLevel.ROJO
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
     * Normaliza el valor indicado para transformarlo en un valor
     * en la escala de 0 a 1, según el valor mínimo y máximo especificados.
     *
     * @param value Valor a normalizar.
     * @param min Valor mínimo.
     * @param max Valor máximo.
     * @return Valor Double normalizado.
     */
    private fun normalize(value: Double, min: Double, max: Double): Double {
        // Truncar el valor si supera alguno de los límites.
        val v = when {
            value < min -> {min}
            value > max -> {max}
            else -> value
        }
        return (v - min) / (max - min)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RiskContact

        if (contactLocations != other.contactLocations) return false
        if (riskLevel != other.riskLevel) return false
        if (riskPercent != other.riskPercent) return false
        if (riskScore != other.riskScore) return false
        if (exposeTime != other.exposeTime) return false
        if (meanProximity != other.meanProximity) return false
        if (meanTimeInterval != other.meanTimeInterval) return false
        if (startDate != other.startDate) return false
        if (endDate != other.endDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = contactLocations.hashCode()
        result = 31 * result + riskLevel.hashCode()
        result = 31 * result + riskPercent.hashCode()
        result = 31 * result + riskScore.hashCode()
        result = 31 * result + exposeTime.hashCode()
        result = 31 * result + meanProximity.hashCode()
        result = 31 * result + meanTimeInterval.hashCode()
        result = 31 * result + startDate.hashCode()
        result = 31 * result + endDate.hashCode()
        return result
    }

}
