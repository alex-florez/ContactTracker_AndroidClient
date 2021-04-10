package es.uniovi.eii.contacttracker.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import es.uniovi.eii.contacttracker.model.LocationAlarm
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.room.converters.DBConverters
import es.uniovi.eii.contacttracker.room.daos.LocationAlarmDao
import es.uniovi.eii.contacttracker.room.daos.UserLocationDao

/**
 * Clase abstracta que representa la base de datos de la App
 * y que contiene la cabecera de los Dao's.
 */
@Database(entities = [UserLocation::class, LocationAlarm::class], version = 1)
@TypeConverters(DBConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userLocationDao(): UserLocationDao
    abstract fun locationAlarmDao(): LocationAlarmDao

    companion object {
        // Singleton
        const val DB_NAME = "contacttracker.db"
    }
}