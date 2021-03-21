package es.uniovi.eii.contacttracker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.uniovi.eii.contacttracker.location.listeners.callbacks.LocationUpdateCallback
import es.uniovi.eii.contacttracker.location.listeners.callbacks.RegisterLocationCallback
import es.uniovi.eii.contacttracker.location.trackers.FusedLocationTracker
import es.uniovi.eii.contacttracker.location.trackers.LocationManagerTracker
import es.uniovi.eii.contacttracker.location.trackers.LocationTracker
import javax.inject.Named
import javax.inject.Singleton

/**
 * Módulo de dependencias a nivel de servicios de localización.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    // CALLBACK DE LOCALIZACIÓN
    // ************************
    @Binds
    @Singleton
    abstract fun bindLocationUpdateCallback(
        registerLocationCallback: RegisterLocationCallback
    ): LocationUpdateCallback


    // TRACKERS
    // ********************************
    @Binds
    @Singleton
    @Named("fused_location")
    abstract fun bindFusedLocationTracker(
        fusedLocationTracker: FusedLocationTracker
    ): LocationTracker

    @Binds
    @Singleton
    @Named("location_manager")
    abstract fun bindLocationManagerTracker(
        locationManagerTracker: LocationManagerTracker
    ): LocationTracker

}