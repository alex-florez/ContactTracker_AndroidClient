package es.uniovi.eii.contacttracker.room.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.uniovi.eii.contacttracker.model.UserLocation

/**
 * Data Access Object para acceder a los datos de localización
 * del usuario, almacenados en la base de datos.
 */
@Dao
interface UserLocationDao {

    @Insert
    suspend fun insert(userLocation: UserLocation): Long

    @Query("SELECT * FROM user_locations ORDER BY locationTimestamp DESC")
    fun getAll(): LiveData<List<UserLocation>>

    @Query("SELECT * FROM user_locations WHERE date(locationTimestamp) = :date ORDER BY locationTimestamp DESC")
    fun getAllByDateString(date: String): LiveData<List<UserLocation>>

    @Query("DELETE FROM user_locations")
    suspend fun deleteAll(): Int
}