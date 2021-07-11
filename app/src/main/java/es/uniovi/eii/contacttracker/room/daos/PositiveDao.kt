package es.uniovi.eii.contacttracker.room.daos

import androidx.room.*
import es.uniovi.eii.contacttracker.model.Positive
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.room.relations.PositiveUserLocationCrossRef
import es.uniovi.eii.contacttracker.room.relations.PositiveWithLocations

/**
 * DAO para los objetos Positive.
 */
@Dao
interface PositiveDao {

    @Insert
    suspend fun insert(positiveUserLocationCrossRef: PositiveUserLocationCrossRef)

    @Insert
    suspend fun insert(positive: Positive): Long

    @Transaction
    @Query("SELECT * FROM positives ORDER BY timestamp DESC")
    suspend fun getAllPositives(): List<PositiveWithLocations>
}