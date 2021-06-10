package es.uniovi.eii.contacttracker.model

/**
 * Clase que representa el resultado de la comprobación de contactos de riesgo.
 * Contiene una lista de Tramos de contacto, en los cuales ha habido riesgo
 * de contacto con el positivo.
 */
class RiskContactResult {

    /**
     * Lista de contactos de riesgo.
     */
    val riskContacts: MutableList<RiskContact> = mutableListOf()

}