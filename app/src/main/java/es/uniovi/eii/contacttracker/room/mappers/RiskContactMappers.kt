package es.uniovi.eii.contacttracker.room.mappers

import es.uniovi.eii.contacttracker.room.relations.ResultWithRiskContacts
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.room.relations.RiskContactWithLocations

/**
 * Mapea el objeto wrapper del resultado de una comprobación y sus contactos de
 * riesgo para transformarlo en un objeto RiskContactResult.
 *
 * @param resultWithRiskContacts Objeto Wrapper con el resultado y los contactos de riesgo.
 * @return Objeto RiskContactResult.
 */
fun toRiskContactResult(resultWithRiskContacts: ResultWithRiskContacts): RiskContactResult {
    val rcr = resultWithRiskContacts.riskContactResult
    val riskContactResult = RiskContactResult()
    riskContactResult.resultId = rcr.resultId
    riskContactResult.numberOfPositives = rcr.numberOfPositives
    riskContactResult.timestamp = rcr.timestamp
    // Transformar RiskContacts
    resultWithRiskContacts.riskContacts.forEach {
        riskContactResult.riskContacts.add(toRiskContact(it))
    }
    return riskContactResult
}

/**
 * Transforma el objeto wrapper que contiene un contacto de riesgo y sus
 * localizaciones asociadas en un único objeto de tipo RiskContact.
 *
 * @param riskContactWithLocations Objeto wrapper (envoltorio) de un contacto de riesgo y sus localizaciones.
 * @return Objeto mapeado de tipo RiskContact.
 */
fun toRiskContact(riskContactWithLocations: RiskContactWithLocations): RiskContact {
    val rc = riskContactWithLocations.riskContact
    val riskContact = RiskContact()
    riskContact.riskContactId = rc.riskContactId
    riskContact.riskContactResultId = rc.riskContactResultId
    riskContact.riskLevel = rc.riskLevel
    riskContact.riskScore = rc.riskScore
    riskContact.riskPercent = rc.riskPercent
    riskContact.exposeTime = rc.exposeTime
    riskContact.meanProximity = rc.meanProximity
    riskContact.meanTimeInterval = rc.meanTimeInterval
    riskContact.startDate = rc.startDate
    riskContact.endDate = rc.endDate
    riskContact.contactLocations = riskContactWithLocations.riskContactLocations.toMutableList()
    riskContact.positiveLabel = rc.positiveLabel
    riskContact.timeIntersection = rc.timeIntersection
    riskContact.config = rc.config
    return riskContact
}

/**
 * Transforma el objeto resultado del dominio en un objeto auxiliar
 * para la base de datos que representa la relación 1-n entre un resultado
 * y sus contactos de riesgo.
 *
 * @param riskContactResult Objeto resultado del dominio.
 * @return Objeto auxiliar para la relación entre un resultado y sus contactos de riesgo.
 */
fun toResultWithRiskContacts(riskContactResult: RiskContactResult): ResultWithRiskContacts {
    val riskContacts = mutableListOf<RiskContactWithLocations>()
    riskContactResult.riskContacts.forEach {
        riskContacts.add(toRiskContactWithLocations(it))
    }
    return ResultWithRiskContacts(
        riskContactResult,
        riskContacts
    )
}

/**
 * Transforma el contacto de riesgo del dominio a un objeto auxiliar para
 * la base de datos que representa la relación 1-n entre un contacto de riesgo
 * y sus localizaciones de contacto.
 *
 * @param riskContact Objeto contacto de riesgo del dominio.
 * @return Objeto auxiliar para la relación entre un contacto de riesgo y sus localizaciones.
 */
fun toRiskContactWithLocations(riskContact: RiskContact): RiskContactWithLocations{
    return RiskContactWithLocations(
        riskContact,
        riskContact.contactLocations
    )
}