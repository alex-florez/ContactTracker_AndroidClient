package es.uniovi.eii.contacttracker.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import es.uniovi.eii.contacttracker.location.alarms.LocationAlarm
import es.uniovi.eii.contacttracker.model.*
import es.uniovi.eii.contacttracker.riskcontact.alarms.RiskContactAlarm
import es.uniovi.eii.contacttracker.room.converters.DBConverters
import es.uniovi.eii.contacttracker.room.daos.*
import es.uniovi.eii.contacttracker.room.relations.PositiveUserLocationCrossRef

/**
 * Clase abstracta que representa la base de datos de la App
 * y que contiene la cabecera de los Dao's.
 */
@Database(entities = [UserLocation::class, LocationAlarm::class,
    RiskContactResult::class, RiskContact::class, RiskContactLocation::class,
    Positive::class, PositiveUserLocationCrossRef::class, RiskContactAlarm::class], version = 1)
@TypeConverters(DBConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userLocationDao(): UserLocationDao
    abstract fun locationAlarmDao(): LocationAlarmDao
    abstract fun riskContactDao(): RiskContactDao
    abstract fun positiveDao(): PositiveDao
    abstract fun riskContactAlarmDao(): RiskContactAlarmDao
    companion object {
        const val DB_NAME = "contacttracker.db"
    }
}