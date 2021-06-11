package es.uniovi.eii.contacttracker.mappers

import es.uniovi.eii.contacttracker.model.ResultWithRiskContacts
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.model.RiskContactResult

/**
 * Mapea el objeto wrapper del resultado de una comprobación y sus contactos de
 * riesgo para transformarlo en un objeto RiskContactResult.
 *
 * @param resultWithRiskContacts Objeto Wrapper con el resultado y los contactos de riesgo.
 * @return Objeto RiskContactResult.
 */
fun toRiskContactResult(resultWithRiskContacts: ResultWithRiskContacts): RiskContactResult {
    val riskContactResult = RiskContactResult()
    riskContactResult.resultId = resultWithRiskContacts.riskContactResult.resultId
    riskContactResult.numberOfPositives = resultWithRiskContacts.riskContactResult.numberOfPositives
    riskContactResult.timestamp = resultWithRiskContacts.riskContactResult.timestamp
    // Transformar RiskContacts
    resultWithRiskContacts.riskContacts.forEach {
        val riskContact = RiskContact()
        riskContact.riskContactId = it.riskContact.riskContactId
        riskContact.riskContactResultId = it.riskContact.riskContactResultId
        riskContact.riskLevel = it.riskContact.riskLevel
        riskContact.riskScore = it.riskContact.riskScore
        riskContact.exposeTime = it.riskContact.exposeTime
        riskContact.meanProximity = it.riskContact.meanProximity
        riskContact.meanTimeInterval = it.riskContact.meanTimeInterval
        riskContact.startDate = it.riskContact.startDate
        riskContact.endDate = it.riskContact.endDate
        riskContact.contactLocations = it.riskContactLocations.toMutableList()
        riskContactResult.riskContacts.add(riskContact)
    }
    return riskContactResult
}