package es.uniovi.eii.contacttracker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.network.api.PositiveAPI
import es.uniovi.eii.contacttracker.repositories.AlarmRepository
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.repositories.PositiveRepository
import es.uniovi.eii.contacttracker.room.AppDatabase
import es.uniovi.eii.contacttracker.room.daos.LocationAlarmDao
import es.uniovi.eii.contacttracker.room.daos.PositiveDao
import es.uniovi.eii.contacttracker.room.daos.RiskContactDao
import es.uniovi.eii.contacttracker.room.daos.UserLocationDao
import javax.inject.Singleton

/**
 * Módulo a nivel de Base de datos para propocionar
 * las dependencias necesarias relativas a la BDD.
 */
@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    /* Fichero de Shared Preferences de la aplicación */
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext ctx: Context): SharedPreferences {
       return ctx.getSharedPreferences(ctx.getString(R.string.shared_prefs_file_name), Context.MODE_PRIVATE)
    }

    /* Base de datos de la App */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appCtx: Context): AppDatabase {
        return Room.databaseBuilder(
            appCtx,
            AppDatabase::class.java,
            AppDatabase.DB_NAME).build()
    }

    // DAOs
    // ******************

    @Provides
    @Singleton
    fun provideUserLocationDao(appDB: AppDatabase): UserLocationDao {
        return appDB.userLocationDao()
    }

    @Provides
    @Singleton
    fun provideLocationAlarmDao(appDB: AppDatabase): LocationAlarmDao {
        return appDB.locationAlarmDao()
    }

    @Provides
    @Singleton
    fun provideRiskContactDao(appDB: AppDatabase): RiskContactDao {
        return appDB.riskContactDao()
    }

    @Provides
    @Singleton
    fun providePositiveDao(appDB: AppDatabase): PositiveDao {
        return appDB.positiveDao()
    }

    // Repositories
    // ******************
    @Provides
    @Singleton
    fun provideLocationRepository(userLocationDao: UserLocationDao): LocationRepository {
        return LocationRepository(userLocationDao)
    }

    @Provides
    @Singleton
    fun provideAlarmRepository(@ApplicationContext ctx: Context,
                               locationAlarmDao: LocationAlarmDao): AlarmRepository {
        return AlarmRepository(ctx, locationAlarmDao)
    }

    @Provides
    @Singleton
    fun providePositiveRepository(positiveAPI: PositiveAPI,
                                    positiveDao: PositiveDao): PositiveRepository {
        return PositiveRepository(positiveAPI, positiveDao)
    }


}