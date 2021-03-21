package es.uniovi.eii.contacttracker.location.trackers

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.location.LocationUpdateMode
import es.uniovi.eii.contacttracker.location.listeners.callbacks.LocationUpdateCallback
import javax.inject.Inject

/**
 * Implementación concreta de la interfaz LcoationTracker.
 * Representa un rastreador de ubicación basado en los servicios de GooglePlay
 * que utiliza un Fused Provider.
 */
class FusedLocationTracker @Inject constructor(
    @ApplicationContext ctx: Context
) : AbstractLocationTracker(ctx) {

    /**
     * API Fused
     */
    private val fusedLocationProvider: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ctx)

    /**
     * Callback de llamada
     */
    private var locationCallback: LocationCallback

    init {
        // Callback por defecto
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(result: LocationResult?) {
                super.onLocationResult(result)
            }
        }
    }

    override fun setCallback(callback: LocationUpdateCallback) {
       locationCallback = object : LocationCallback(){
           override fun onLocationResult(result: LocationResult?) {
               result?.let {
                   callback.onLocationUpdate(it.lastLocation)
               }
           }
       }
    }

    @SuppressLint("MissingPermission")
    override fun startLocationUpdates(mode: LocationUpdateMode): Boolean {
        val locationRequest = LocationRequest()
        locationRequest.priority = locationTrackRequest.priority
        locationRequest.interval = locationTrackRequest.minInterval
        locationRequest.fastestInterval = locationTrackRequest.fastestInterval
        locationRequest.smallestDisplacement = locationTrackRequest.smallestDisplacement
        return when(mode){
            LocationUpdateMode.CALLBACK_MODE -> {
                fusedLocationProvider.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.getMainLooper())
                true
            }
            LocationUpdateMode.PENDING_INTENT_MODE -> {
                locationPendingIntent?.let {
                    fusedLocationProvider.requestLocationUpdates(
                    locationRequest,
                    it)}
                true
            }
        }
    }

    override fun stopLocationUpdates(mode: LocationUpdateMode): Boolean {
        return when(mode){
            LocationUpdateMode.CALLBACK_MODE -> {
                fusedLocationProvider.removeLocationUpdates(locationCallback)
                true
            }
            LocationUpdateMode.PENDING_INTENT_MODE -> {
                locationPendingIntent?.let {
                    fusedLocationProvider.removeLocationUpdates(it)
                }
                true
            }
        }
    }

    override fun getTrackerTag(): String {
        return TAG
    }


    companion object {
        // TAG
        private const val TAG = "FusedLocationTracker"
    }

}