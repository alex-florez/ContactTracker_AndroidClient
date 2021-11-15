package es.uniovi.eii.contacttracker.room.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.model.RiskContactLocation
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.room.relations.ResultWithRiskContacts

/**
 * Data Access Object para acceder a los resultados de la
 * comprobación de contactos de riesgo.
 */
@Dao
interface RiskContactDao {

    @Transaction
    @Insert
    suspend fun insert(riskContactResult: RiskContactResult): Long

    @Transaction
    @Insert
    suspend fun insertRiskContact(riskContact: RiskContact): Long

    @Transaction
    @Insert
    suspend fun insertRiskContactLocations(riskContactLocations: List<RiskContactLocation>)

    @Query("SELECT * FROM risk_contact_results ORDER BY timestamp DESC")
    fun getAllRiskContactResults(): LiveData<List<ResultWithRiskContacts>>

    @Query("SELECT * FROM risk_contact_results WHERE resultId = :id")
    fun getResultById(id: Long): ResultWithRiskContacts?

}