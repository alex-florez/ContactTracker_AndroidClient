package es.uniovi.eii.contacttracker.network.model

/**
 * Clase utilizada en las Estadísticas.
 *
 * Modela un resultado de una comprobación de contactos de riesgo, que será
 * subido a la nube para usarlo en cálculos estadísticos.
 */
data class CheckResult(
    val timestamp: Long, // Fecha de comprobación
    val riskPercent: Double, // Puntuación de riesgo (en %)
    val exposeTime: Long, // Tiempo de exposición (milisegundos)
    val proximity: Double // Proximidad
)
