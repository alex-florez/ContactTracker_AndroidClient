package es.uniovi.eii.contacttracker.room.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import es.uniovi.eii.contacttracker.location.alarms.LocationAlarm

/**
 * Data Access Object para acceder a los datos
 * de las alarmas de localización almacenados en SQLite.
 */
@Dao
interface LocationAlarmDao {

    @Insert
    suspend fun insert(locationAlarm: LocationAlarm): Long

    @Update
    suspend fun update(locationAlarm: LocationAlarm): Int

    @Query("SELECT * FROM location_alarms ORDER BY creationDate DESC")
    fun getAll(): LiveData<List<LocationAlarm>>

    @Query("SELECT * FROM location_alarms WHERE id = :alarmID")
    suspend fun getByID(alarmID: Long): LocationAlarm?

    @Query("UPDATE location_alarms SET active = :enable WHERE id = :alarmID")
    suspend fun toggleState(alarmID: Long, enable: Boolean): Int

    @Query("DELETE FROM location_alarms")
    suspend fun deleteAll(): Int

    @Query("DELETE FROM location_alarms WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT * FROM location_alarms WHERE (time(startDate) >= time(:newStartDate) AND time(startDate) <= time(:newEndDate)) OR (time(endDate) >= time(:newStartDate) AND time(endDate) <= time(:newEndDate) OR (time(startDate) < time(:newStartDate) AND time(endDate) > time(:newEndDate)))")
    suspend fun getCollisions(newStartDate: String, newEndDate: String): List<LocationAlarm>
}