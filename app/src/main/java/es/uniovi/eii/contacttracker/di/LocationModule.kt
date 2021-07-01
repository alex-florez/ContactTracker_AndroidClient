package es.uniovi.eii.contacttracker.di

import android.app.AlarmManager
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.uniovi.eii.contacttracker.location.listeners.callbacks.LocationUpdateCallback
import es.uniovi.eii.contacttracker.location.listeners.callbacks.RegisterLocationCallback
import es.uniovi.eii.contacttracker.location.trackers.FusedLocationTracker
import es.uniovi.eii.contacttracker.location.trackers.LocationManagerTracker
import es.uniovi.eii.contacttracker.location.trackers.LocationTracker
import es.uniovi.eii.contacttracker.riskcontact.detector.RiskContactDetector
import es.uniovi.eii.contacttracker.riskcontact.detector.RiskContactDetectorImpl
import javax.inject.Named
import javax.inject.Singleton

/**
 * Módulo de dependencias a nivel de servicios de localización
 * para rastrear la ubicación y comprobación de contactos de riesgo.
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

    // DETECTOR DE CONTACTOS DE RIESGO
    // *******************************************************
    @Binds
    @Singleton
    abstract fun bindRiskContactDetector(
            riskContactDetectorImpl: RiskContactDetectorImpl
    ): RiskContactDetector

}