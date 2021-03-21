package es.uniovi.eii.contacttracker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.uniovi.eii.contacttracker.location.listeners.callbacks.LocationUpdateCallback
import es.uniovi.eii.contacttracker.location.listeners.callbacks.RegisterLocationCallback
import javax.inject.Singleton

/**
 * Módulo de dependencias a nivel de servicios de localización.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationUpdateCallback(
        registerLocationCallback: RegisterLocationCallback
    ): LocationUpdateCallback
}