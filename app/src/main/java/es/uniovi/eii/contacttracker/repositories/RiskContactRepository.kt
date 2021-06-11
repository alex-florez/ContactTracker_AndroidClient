package es.uniovi.eii.contacttracker.repositories

import es.uniovi.eii.contacttracker.model.ResultWithRiskContacts
import es.uniovi.eii.contacttracker.model.RiskContactWithLocation
import es.uniovi.eii.contacttracker.room.daos.RiskContactDao
import javax.inject.Inject

class RiskContactRepository @Inject constructor(
        private val riskContactDao: RiskContactDao
){

    suspend fun insert(resultWithRiskContacts: ResultWithRiskContacts) {
        val id = riskContactDao.insert(resultWithRiskContacts.riskContactResult)

        for(riskContactWithLoc in resultWithRiskContacts.riskContacts){
           riskContactWithLoc.riskContact.riskContactResultId = id
            val rcId = riskContactDao.insertRiskContact(riskContactWithLoc.riskContact)
            for(loc in riskContactWithLoc.riskContactLocations){
                loc.rcId = rcId
            }
            riskContactDao.insertRiskContactLocations(riskContactWithLoc.riskContactLocations)
        }
    }

    suspend fun getAll(): List<ResultWithRiskContacts> {
        return riskContactDao.getAllRiskContactResults()
    }
}