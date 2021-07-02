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
    @Query("SELECT * FROM user_locations ORDER BY timestamp DESC")
    fun getAll(): LiveData<List<UserLocation>>

    @Query("SELECT * FROM user_locations WHERE date(timestamp) = :date ORDER BY timestamp DESC")
    fun getAllByDateString(date: String): LiveData<List<UserLocation>>

    @Query("SELECT * FROM user_locations WHERE date(timestamp) = :date ORDER BY timestamp ASC")
    fun getAllByDate(date: String): List<UserLocation>

    @Query("DELETE FROM user_locations")
    suspend fun deleteAll(): Int

    @Query("DELETE FROM user_locations WHERE date(timestamp) = :dateString")
    suspend fun deleteByDate(dateString: String): Int

    @Query("SELECT * FROM user_locations WHERE date(timestamp) = date('now') ORDER BY timestamp DESC")
    suspend fun getToday(): List<UserLocation>

    @Query("SELECT * FROM user_locations WHERE date(timestamp) >= :startDate AND date(timestamp) <= :endDate")
    suspend fun getLocationsBetween(startDate: String, endDate: String): List<UserLocation>

    @Query("SELECT DISTINCT(date(timestamp)) FROM user_locations WHERE date(timestamp) >= :startDate AND date(timestamp) <= :endDate")
    suspend fun getLocationDatesBetween(startDate: String, endDate: String): List<String>


}