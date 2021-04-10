package es.uniovi.eii.contacttracker.room.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.uniovi.eii.contacttracker.model.LocationAlarm

/**
 * Data Access Object para acceder a los datos
 * de las alarmas de localización almacenados en SQLite.
 */
@Dao
interface LocationAlarmDao {

    @Insert
    suspend fun insert(locationAlarm: LocationAlarm): Long

    @Query("SELECT * FROM location_alarms ORDER BY creationDate DESC")
    fun getAll(): LiveData<List<LocationAlarm>>

    @Query("DELETE FROM location_alarms")
    suspend fun deleteAll(): Int

    @Query("DELETE FROM location_alarms WHERE id = :id")
    suspend fun deleteById(id: Long): Int

}