package es.uniovi.eii.contacttracker.di

import android.app.AlarmManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

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
}