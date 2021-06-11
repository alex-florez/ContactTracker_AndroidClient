package es.uniovi.eii.contacttracker.repositories

import es.uniovi.eii.contacttracker.model.ResultWithRiskContacts
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.model.RiskContactWithLocation
import es.uniovi.eii.contacttracker.room.daos.RiskContactDao
import javax.inject.Inject

class RiskContactRepository @Inject constructor(
        private val riskContactDao: RiskContactDao
){

    /**
     * Inserta en la base de datos el resultado de la comprobación
     * de contactos de riesgo.
     *
     * @param riskContactResult Resultado de la comprobación de contactos de riesgo.
     */
    suspend fun insert(riskContactResult: RiskContactResult) {
        // Crear objeto wrapper para cada Risk Contact
        val riskContacts = mutableListOf<RiskContactWithLocation>()
        for(rc in riskContactResult.riskContacts) {
            riskContacts.add(RiskContactWithLocation(rc, rc.contactLocations))
        }
        // Crear objeto Wrapper
        val wrapper = ResultWithRiskContacts(
                riskContactResult,
                riskContacts
        )
        // Insertar Resultado
        val resultId = riskContactDao.insert(wrapper.riskContactResult)
        // Insertar Contactos de Riesgo
        for(riskContactWithLocation in wrapper.riskContacts){
            riskContactWithLocation.riskContact.riskContactResultId = resultId // ID del resultado.
            val riskContactId = riskContactDao.insertRiskContact(riskContactWithLocation.riskContact)
            for(contactPoint in riskContactWithLocation.riskContactLocations){
                contactPoint.rcId = riskContactId // ID del Contacto de Riesgo.
            }
            // Insertar Puntos de Contacto
            riskContactDao.insertRiskContactLocations(riskContactWithLocation.riskContactLocations)
        }
    }

    suspend fun getAll(): List<RiskContactResult> {
        // Obtener lista de objetos wrapper
        val wrapperList = riskContactDao.getAllRiskContactResults()
        val result = mutableListOf<RiskContactResult>()
        wrapperList.forEach { wrapper ->
            val riskContactResult = RiskContactResult()
            riskContactResult.resultId = wrapper.riskContactResult.resultId
            riskContactResult.numberOfPositives = wrapper.riskContactResult.numberOfPositives
            riskContactResult.timestamp = wrapper.riskContactResult.timestamp
            // Transformar RiskContacts
            wrapper.riskContacts.forEach {
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
            result.add(riskContactResult)
        }
        return result
    }
}