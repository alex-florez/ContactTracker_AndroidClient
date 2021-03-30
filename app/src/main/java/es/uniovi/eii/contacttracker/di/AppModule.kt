package es.uniovi.eii.contacttracker.di

import android.app.AlarmManager
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.uniovi.eii.contacttracker.room.AppDatabase
import javax.inject.Singleton

/**
 * Módulo a nivel de Aplicación para proporcionar
 * las dependencias necesarias.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

}