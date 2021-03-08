package es.uniovi.eii.contacttracker.location.trackers

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import es.uniovi.eii.contacttracker.location.LocationTrackRequest
import es.uniovi.eii.contacttracker.location.LocationTracker
import es.uniovi.eii.contacttracker.location.LocationUpdateMode
import es.uniovi.eii.contacttracker.location.callbacks.LocationUpdateCallback
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.util.PermissionUtils

/**
 * Implementación concreta de la interfaz LcoationTracker.
 * Representa un rastreador de ubicación basado en los servicios de GooglePlay
 * que utiliza un Fused Provider.
 */
class FusedLocationTracker(
        private val ctx: Context
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

//    override fun setLocationRequest(request: LocationTrackRequest) {
//       locationRequest = LocationRequest()
//        locationRequest.priority = request.priority
//        locationRequest.interval = request.minInterval
//        locationRequest.smallestDisplacement = request.smallestDisplacement
//    }

    override fun setCallback(callback: LocationUpdateCallback) {
       locationCallback = object : LocationCallback(){
           override fun onLocationResult(result: LocationResult?) {
               result?.let {
                   callback.onLocationUpdate(it.lastLocation)
               }
           }
       }
    }

//
//    @SuppressLint("MissingPermission")
//    override fun start(mode: LocationUpdateMode): Boolean {
//        if(PermissionUtils.check(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION)){ // Permisos
//            if(LocationUtils.checkGPS(ctx)){ // Ubicación activada
//                when(mode){
//                    LocationUpdateMode.CALLBACK_MODE -> {
//                        fusedLocationProvider.requestLocationUpdates(
//                                locationRequest,
//                                locationCallback,
//                                Looper.myLooper()
//                        )
//                        Log.d(TAG, "Rastreo de ubicación iniciado. (Modo: ${mode.name})")
//                        return true;
//                    }
//                    LocationUpdateMode.PENDING_INTENT_MODE -> {
//                        fusedLocationProvider.requestLocationUpdates(
//                                locationRequest,
//                                locationPendingIntent
//                        )
//                        Log.d(TAG, "Rastreo de ubicacion iniciado. (Modo: ${mode.name})")
//                        return true
//                    }
//                    else -> {
//
//                    }
//                }
//            }
//        }
//       return false
//    }
//
//    override fun stop(mode: LocationUpdateMode): Boolean {
//        when(mode){
//            LocationUpdateMode.CALLBACK_MODE -> {
//                fusedLocationProvider.removeLocationUpdates(locationCallback)
//                Log.d(TAG, "Rastreo de ubicación detenido. (Modo: ${mode.name})")
//                return true
//            }
//            LocationUpdateMode.PENDING_INTENT_MODE -> {
//                fusedLocationProvider.removeLocationUpdates(locationPendingIntent)
//                Log.d(TAG, "Rastreo de ubicación detenido. (Modo: ${mode.name})")
//                return true
//            }
//            else -> {
//
//            }
//        }
//        return false
//    }

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
                                Looper.myLooper())
                true
            }
            LocationUpdateMode.PENDING_INTENT_MODE -> {
                fusedLocationProvider.requestLocationUpdates(
                                locationRequest,
                                locationPendingIntent)
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
                fusedLocationProvider.removeLocationUpdates(locationPendingIntent)
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