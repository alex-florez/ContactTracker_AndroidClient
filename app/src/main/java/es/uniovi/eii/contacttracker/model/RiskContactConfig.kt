package es.uniovi.eii.contacttracker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/* Valores por defecto */
private const val SCOPE = 3
private val EXPOSE_TIME_RANGE = arrayOf(0L, 900000L)
private val MEAN_PROXIMITY_RANGE = arrayOf(0.0, 10.0)
private val MEAN_TIME_INTERVAL_RANGE = arrayOf(0L, 600000L)
private const val EXPOSE_TIME_WEIGHT = 0.3
private const val MEAN_PROXIMITY_WEIGHT = 0.5
private const val MEAN_TIME_INTERVAL_WEIGHT = 0.2

/**
 * Clase de datos que contiene todos los parámetros de
 * configuración necesarios para llevar a cabo la comprobación
 * de contactos de riesgo.
 */
@Parcelize
data class RiskContactConfig(
    var checkScope: Int = SCOPE, // Alcance de la comprobación en días.
    val exposeTimeRange: Array<Long> = EXPOSE_TIME_RANGE, // Rango de tiempo de exposición (milisegundos)
    val meanProximityRange: Array<Double> = MEAN_PROXIMITY_RANGE, // Rango de proximidad media (metros)
    val meanTimeIntervalRange: Array<Long> = MEAN_TIME_INTERVAL_RANGE, // Rango de intervalo de tiempo medio (milisegundos)
    val exposeTimeWeight: Double = EXPOSE_TIME_WEIGHT, // Porcentaje de peso para el tiempo de exposición
    val meanProximityWeight: Double = MEAN_PROXIMITY_WEIGHT, // Porcentaje de peso para la proximidad media
    val meanTimeIntervalWeight: Double = MEAN_TIME_INTERVAL_WEIGHT// Porcentaje de peso para el intervalo de tiempo medio
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RiskContactConfig

        if (checkScope != other.checkScope) return false
        if (!exposeTimeRange.contentEquals(other.exposeTimeRange)) return false
        if (!meanProximityRange.contentEquals(other.meanProximityRange)) return false
        if (!meanTimeIntervalRange.contentEquals(other.meanTimeIntervalRange)) return false
        if (exposeTimeWeight != other.exposeTimeWeight) return false
        if (meanProximityWeight != other.meanProximityWeight) return false
        if (meanTimeIntervalWeight != other.meanTimeIntervalWeight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = checkScope
        result = 31 * result + exposeTimeRange.contentHashCode()
        result = 31 * result + meanProximityRange.contentHashCode()
        result = 31 * result + meanTimeIntervalRange.contentHashCode()
        result = 31 * result + exposeTimeWeight.hashCode()
        result = 31 * result + meanProximityWeight.hashCode()
        result = 31 * result + meanTimeIntervalWeight.hashCode()
        return result
    }
}
