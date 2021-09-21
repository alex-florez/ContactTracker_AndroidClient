package es.uniovi.eii.contacttracker.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import es.uniovi.eii.contacttracker.riskcontact.alarms.RiskContactAlarm

/**
 * DAO para acceder a los datos de las alarmas de comprobación de contactos.
 */
@Dao
interface RiskContactAlarmDao {

    @Insert
    suspend fun insert(riskContactAlarm: RiskContactAlarm): Long

    @Update
    suspend fun update(riskContactAlarms: List<RiskContactAlarm>)

    @Query("DELETE FROM risk_contact_alarms WHERE id = :alarmID")
    suspend fun remove(alarmID: Long)

    @Query("SELECT * FROM risk_contact_alarms")
    suspend fun getAll(): List<RiskContactAlarm>

    @Query("SELECT * FROM risk_contact_alarms WHERE id = :id")
    suspend fun getById(id: Long): RiskContactAlarm?

    @Query("SELECT * FROM risk_contact_alarms WHERE time(startDate) = :hour")
    suspend fun getAlarmsBySetHour(hour: String): List<RiskContactAlarm>
}