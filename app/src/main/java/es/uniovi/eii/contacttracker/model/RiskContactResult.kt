package es.uniovi.eii.contacttracker.model

/**
 * Clase que representa un resultado
 * de la comprobación de contactos de riesgo.
 */
class RiskContactResult {

    /**
     * Lista de contactos de riesgo.
     */
    val riskContacts: MutableList<RiskContact> = mutableListOf()

}