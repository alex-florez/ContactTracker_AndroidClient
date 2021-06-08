package es.uniovi.eii.contacttracker.model

/**
 * Clase de datos que representa un resultado
 * de la comprobación de contactos de riesgo.
 */
data class RiskContactResult(
    val riskContacts: List<RiskContact>
)