package es.uniovi.eii.contacttracker.di

import android.app.AlarmManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.uniovi.eii.contacttracker.location.alarms.LocationAlarmManager
import es.uniovi.eii.contacttracker.repositories.AlarmRepository
import javax.inject.Singleton

/**
 * Módulo de inyección de dependencias para las alarmas
 * de localización.
 */
@Module
@InstallIn(SingletonComponent::class)
object AlarmModule {

    // ALARMAS
    // ***************************************
    @Provides
    fun provideAlarmManager(
            @ApplicationContext ctx: Context
    ): AlarmManager {
        return ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Provides
    @Singleton
    fun provideLocationAlarmManager(am: AlarmManager,
                                    alarmRepository: AlarmRepository,
                                    @ApplicationContext ctx: Context): LocationAlarmManager {
        return LocationAlarmManager(am, alarmRepository, ctx)
    }
}