package es.uniovi.eii.contacttracker.room.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.uniovi.eii.contacttracker.model.UserLocation
import java.util.Date

/**
 * Data Access Object para acceder a los datos de localización
 * del usuario, almacenados en la base de datos.
 */
@Dao
interface UserLocationDao {

    @Insert
    suspend fun insert(userLocation: UserLocation): Long

    @Insert
    fun insertLoc(userLocation: UserLocation)

    // QUERY'S
    // *********************************************************************************************
    @Query("SELECT * FROM user_locations ORDER BY locationTimestamp DESC")
    fun getAll(): LiveData<List<UserLocation>>

    @Query("SELECT * FROM user_locations WHERE date(locationTimestamp) = :date ORDER BY locationTimestamp DESC")
    fun getAllByDateString(date: String): LiveData<List<UserLocation>>

    @Query("SELECT * FROM user_locations WHERE date(locationTimestamp) = :date ORDER BY locationTimestamp ASC")
    fun getAllByDate(date: String): List<UserLocation>

    @Query("DELETE FROM user_locations")
    suspend fun deleteAll(): Int

    @Query("DELETE FROM user_locations WHERE date(locationTimestamp) = :dateString")
    suspend fun deleteByDate(dateString: String): Int

    @Query("SELECT * FROM user_locations WHERE date(locationTimestamp) = date('now') ORDER BY locationTimestamp DESC")
    suspend fun getToday(): List<UserLocation>

    @Query("SELECT * FROM user_locations WHERE date(locationTimestamp) >= :startDate AND date(locationTimestamp) <= :endDate")
    suspend fun getLocationsBetween(startDate: String, endDate: String): List<UserLocation>

    @Query("SELECT DISTINCT(date(locationTimestamp)) FROM user_locations WHERE date(locationTimestamp) >= :startDate AND date(locationTimestamp) <= :endDate")
    suspend fun getLocationDatesBetween(startDate: String, endDate: String): List<String>


}