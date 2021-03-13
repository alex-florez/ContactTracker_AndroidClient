package es.uniovi.eii.contacttracker.location.trackers

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import es.uniovi.eii.contacttracker.location.LocationUpdateMode
import es.uniovi.eii.contacttracker.location.callbacks.LocationUpdateCallback

/**
 * Implementación concreta de rastreador de ubicación, que utiliza la
 * API nativa de Android LocationManager.
 */
class LocationManagerTracker(private val ctx: Context) : AbstractLocationTracker(ctx) {

    /**
     * API Location Manager
     */
    private val locationManager: LocationManager =
            ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    /**
     * Location Listener (callback)
     */
    private var locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }
    }

//    @SuppressLint("MissingPermission")
//    override fun start(mode: LocationUpdateMode): Boolean {
//        if(PermissionUtils.check(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION)){
//            if(LocationUtils.checkGPS(ctx)){
//                when(mode) {
//                    LocationUpdateMode.CALLBACK_MODE -> {
//                        locationManager.requestLocationUpdates(locationProvider,
//                                locationRequest.minInterval,
//                                locationRequest.smallestDisplacement,
//                                locationListener)
//                        Log.d(TAG, "Rastreo de ubicación iniciado (Modo: ${mode.name})")
//                        return true
//                    }
//                    LocationUpdateMode.PENDING_INTENT_MODE -> {
//                        locationPendingIntent?.let {
//                            locationManager.requestLocationUpdates(locationProvider,
//                                    locationRequest.minInterval,
//                                    locationRequest.smallestDisplacement,
//                                    it)
//                        }
//                        Log.d(TAG, "Rastreo de ubicación iniciado (Modo: ${mode.name})")
//                        return true
//                    }
//                    else -> {
//
//                    }
//                }
//            }
//        }
//        return false
//    }

//    @SuppressLint("MissingPermission")
//    override fun stop(mode: LocationUpdateMode): Boolean {
//        when(mode) {
//            LocationUpdateMode.CALLBACK_MODE -> {
//                locationManager.removeUpdates(locationListener)
//                Log.d(TAG, "Rastreo de ubicación detenido (Modo: ${mode.name})")
//                return true
//            }
//            LocationUpdateMode.PENDING_INTENT_MODE -> {
//                locationPendingIntent?.let {
//                    locationManager.removeUpdates(it)
//                    Log.d(TAG, "Rastreo de ubicación detenido (Modo: ${mode.name})")
//                    return true
//                }
//            }
//            else -> {
//
//            }
//        }
//        return false
//    }

    @SuppressLint("MissingPermission")
    override fun startLocationUpdates(mode: LocationUpdateMode): Boolean {
        return when(mode){
            LocationUpdateMode.CALLBACK_MODE -> {
                locationManager.requestLocationUpdates(locProvider,
                                locationTrackRequest.minInterval,
                                locationTrackRequest.smallestDisplacement,
                                locationListener)
                true
            }
            LocationUpdateMode.PENDING_INTENT_MODE -> {
                locationPendingIntent?.let {
                    locationManager.requestLocationUpdates(locProvider,
                            locationTrackRequest.minInterval,
                            locationTrackRequest.smallestDisplacement,
                            it)
                }
                true
            }
        }
    }

    override fun stopLocationUpdates(mode: LocationUpdateMode): Boolean {
        return when(mode){
            LocationUpdateMode.PENDING_INTENT_MODE -> {
                locationPendingIntent?.let { locationManager.removeUpdates(it) }
                true
            }
            LocationUpdateMode.CALLBACK_MODE -> {
                locationManager.removeUpdates(locationListener)
                true
            }
        }
    }

    override fun getTrackerTag(): String {
        return TAG
    }

    override fun setCallback(callback: LocationUpdateCallback) {
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                callback.onLocationUpdate(location)
            }
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

            }
        }
    }

    companion object {
        // TAG
        private const val TAG = "LocationManagerTracker"
    }
}