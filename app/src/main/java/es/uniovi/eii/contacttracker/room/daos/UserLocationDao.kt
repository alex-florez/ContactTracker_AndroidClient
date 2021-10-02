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

    // QUERY'S
    // *********************************************************************************************
    @Query("SELECT * FROM user_locations ORDER BY timestamp DESC")
    fun getAll(): LiveData<List<UserLocation>>

    @Query("SELECT * FROM user_locations WHERE userlocationID = :id")
    suspend fun getByID(id: Long): UserLocation?

    @Query("SELECT * from user_locations ORDER BY timestamp DESC")
    fun getAllList(): List<UserLocation>

    @Query("SELECT * FROM user_locations WHERE date(timestamp) = :date ORDER BY timestamp DESC")
    fun getAllByDateString(date: String): LiveData<List<UserLocation>>

    @Query("DELETE FROM user_locations")
    suspend fun deleteAll(): Int

    @Query("DELETE FROM user_locations WHERE date(timestamp) = :dateString")
    suspend fun deleteByDate(dateString: String): Int

    @Query("SELECT * FROM user_locations WHERE date(timestamp) >= :startDate AND date(timestamp) <= :endDate ORDER BY timestamp ASC")
    suspend fun getLocationsBetween(startDate: String, endDate: String): List<UserLocation>


}