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

    @Update
    suspend fun update(positive: Positive)

    @Transaction
    @Query("SELECT * FROM positives ORDER BY timestamp DESC")
    suspend fun getAllPositives(): List<PositiveWithLocations>

    @Transaction
    @Query("SELECT * FROM positives WHERE positiveID = :id")
    suspend fun getPositiveByID(id: Long): PositiveWithLocations

    /**
     * Devuelve una lista de Strings con todos los códigos de
     * los positivos almacenados en el almacenamiento local.
     */
    @Query("SELECT positiveCode FROM positives")
    suspend fun getAllPositiveCodes(): List<String>

    /**
     * Devuelve el número de positivos notificados en el día
     * de la fecha pasada como parámetro.
     */
    @Query("SELECT count(*) FROM positives WHERE date(timestamp) = :date")
    suspend fun getNumberOfNotifiedPositivesAt(date: String): Int

    /**
     * Devuelve el último positivo notificado en este dispositivo según el timestamp
     * de notificación.
     */
    @Query("SELECT * FROM positives ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastNotifiedPositive(): Positive?
}