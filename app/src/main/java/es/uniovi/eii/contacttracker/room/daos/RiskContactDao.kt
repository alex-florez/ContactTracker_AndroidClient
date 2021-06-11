package es.uniovi.eii.contacttracker.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.model.RiskContactLocation
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.model.ResultWithRiskContacts

/**
 * Data Access Object para acceder a los resultados de la
 * comprobación de contactos de riesgo.
 */
@Dao
interface RiskContactDao {

    @Transaction
    @Insert
    suspend fun insert(riskContactResult: RiskContactResult): Long

    @Insert
    suspend fun insertRiskContact(riskContact: RiskContact): Long

    @Insert
    suspend fun insertRiskContactLocations(riskContactLocations: List<RiskContactLocation>)

    @Transaction
    @Query("SELECT * FROM RiskContactResult")
    suspend fun getAllRiskContactResults(): List<ResultWithRiskContacts>
}