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
) : LocationTracker {

    /**
     * Petición de localización
     */
    private var locationRequest: LocationRequest

    /**
     * Callback de llamada
     */
    private var locationCallback: LocationCallback

    /**
     * Pending Intent de llamada
     */
    private var locationPendingIntent: PendingIntent? = null

    /**
     * API Fused
     */
    private var fusedLocationProvider: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ctx)


    init {
        // Petición de ubicación por defecto
        locationRequest = LocationRequest()
        locationRequest.interval = defaultMinInterval
        locationRequest.fastestInterval = defaultFastestInterval
        locationRequest.smallestDisplacement = defaultSmallestDisplacement
        locationRequest.priority = defaultPriority

        // Callback por defecto
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
            }
        }
    }

    override fun setLocationRequest(request: LocationTrackRequest) {
       locationRequest = LocationRequest()
        locationRequest.priority = request.priority
        locationRequest.interval = request.minInterval
        locationRequest.smallestDisplacement = request.smallestDisplacement
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

    override fun setIntentCallback(pendingIntent: PendingIntent) {
        locationPendingIntent = pendingIntent
    }

    override fun setLocationProvider(provider: String) {
        // En el Fused Provider no se selecciona el provider.
    }


    @SuppressLint("MissingPermission")
    override fun start(mode: LocationUpdateMode): Boolean {
        if(PermissionUtils.check(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION)){ // Permisos
            if(LocationUtils.checkGPS(ctx)){ // Ubicación activada
                when(mode){
                    LocationUpdateMode.CALLBACK_MODE -> {
                        fusedLocationProvider.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.myLooper()
                        )
                        Log.d(TAG, "Rastreo de ubicación iniciado. (Modo: ${mode.name})")
                        return true;
                    }
                    LocationUpdateMode.PENDING_INTENT_MODE -> {
                        fusedLocationProvider.requestLocationUpdates(
                                locationRequest,
                                locationPendingIntent
                        )
                        Log.d(TAG, "Rastreo de ubicacion iniciado. (Modo: ${mode.name})")
                        return true
                    }
                    else -> {

                    }
                }
            } else {
                LocationUtils.createLocationSettingsAlertDialog(ctx).show() // Abrir diálogo para los ajustes de localización.
            }
        }
       return false
    }

    override fun stop(mode: LocationUpdateMode): Boolean {
        when(mode){
            LocationUpdateMode.CALLBACK_MODE -> {
                fusedLocationProvider.removeLocationUpdates(locationCallback)
                Log.d(TAG, "Rastreo de ubicación detenido. (Modo: ${mode.name})")
                return true
            }
            LocationUpdateMode.PENDING_INTENT_MODE -> {
                fusedLocationProvider.removeLocationUpdates(locationPendingIntent)
                Log.d(TAG, "Rastreo de ubicación detenido. (Modo: ${mode.name})")
                return true
            }
            else -> {

            }
        }
        return false
    }



    companion object {
        // Valores por defecto
        private const val defaultMinInterval: Long = 3000
        private const val defaultFastestInterval: Long = 3000
        private const val defaultSmallestDisplacement: Float = 0f
        private const val defaultPriority:Int = LocationRequest.PRIORITY_HIGH_ACCURACY

        // TAG
        private const val TAG = "FusedLocationTracker"
    }

}