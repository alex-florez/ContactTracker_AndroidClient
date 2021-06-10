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
    var riskContacts: MutableList<RiskContact> = mutableListOf()

    /**
     * Número de positivos con los que se ha entrado en contacto.
     */
    var numberOfPositives: Int = 0

}