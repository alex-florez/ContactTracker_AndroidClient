package es.uniovi.eii.contacttracker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/* Valores por defecto */
private const val SCOPE = 3
private val EXPOSE_TIME_RANGE = Pair(0L, 900000L)
private val MEAN_PROXIMITY_RANGE = Pair(0.0, 10.0)
private val MEAN_TIME_INTERVAL_RANGE = Pair(0L, 600000L)
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
    val checkScope: Int = SCOPE, // Alcance de la comprobación en días.
    val exposeTimeRange: Pair<Long, Long> = EXPOSE_TIME_RANGE, // Rango de tiempo de exposición (milisegundos)
    val meanProximityRange: Pair<Double, Double> = MEAN_PROXIMITY_RANGE, // Rango de proximidad media (metros)
    val meanTimeIntervalRange: Pair<Long, Long> = MEAN_TIME_INTERVAL_RANGE, // Rango de intervalo de tiempo medio (milisegundos)
    val exposeTimeWeight: Double = EXPOSE_TIME_WEIGHT, // Porcentaje de peso para el tiempo de exposición
    val meanProximityWeight: Double = MEAN_PROXIMITY_WEIGHT, // Porcentaje de peso para la proximidad media
    val meanTimeIntervalWeight: Double = MEAN_TIME_INTERVAL_WEIGHT// Porcentaje de peso para el intervalo de tiempo medio
) : Parcelable
