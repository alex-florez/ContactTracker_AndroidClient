package es.uniovi.eii.contacttracker.location.trackers

import android.app.PendingIntent
import android.content.Context
import android.location.LocationManager
import android.util.Log
import es.uniovi.eii.contacttracker.location.LocationTrackRequest
import es.uniovi.eii.contacttracker.location.LocationUpdateMode
import es.uniovi.eii.contacttracker.location.listeners.callbacks.LocationUpdateCallback
import es.uniovi.eii.contacttracker.location.listeners.callbacks.LogLocationCallback
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.util.AndroidUtils

/**
 * Clase abstracta que agrupa el código común de los distintos tipos
 * de rastreadores de ubicación.
 */
abstract class AbstractLocationTracker(private val ctx: Context) : LocationTracker {

    /**
     * Petición de localización.
     */
    protected var locationTrackRequest: LocationTrackRequest = LocationTrackRequest()

    /**
     * Callback de actualización de localización
     */
    protected var locationUpdateCallback: LocationUpdateCallback = LogLocationCallback()

    /**
     * PendingIntent para las actualizaciones de localización.
     */
    protected var locationPendingIntent: PendingIntent? = null

    /**
     * Proveedor de localización.
     */
    protected var locProvider: String = LocationManager.GPS_PROVIDER

    /**
     * Boolean que indica si el tracker está activo o no.
     */
    private var isActive:Boolean = false

    override fun setLocationRequest(request: LocationTrackRequest) {
        locationTrackRequest = request
    }

    override fun setIntentCallback(pendingIntent: PendingIntent) {
        locationPendingIntent = pendingIntent
    }

    override fun setLocationProvider(provider: String) {
       locProvider = provider
    }

    override fun start(mode: LocationUpdateMode): Boolean {
        if(!isActive){
            if(AndroidUtils.check(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    LocationUtils.checkGPS(ctx)){
                        if(startLocationUpdates(mode)) {
                            Log.d(getTrackerTag(), "Rastreo de ubicación iniciado. (Modo: ${mode.name})")
                            isActive = true
                            return true
                        }
            }
        }
        return false
    }

    override fun stop(mode: LocationUpdateMode): Boolean {
        if(isActive){
            if(stopLocationUpdates(mode)){
                Log.d(getTrackerTag(), "Rastreo de ubicación detenido. (Modo: ${mode.name})")
                locationUpdateCallback.onLocationStop()
                isActive = false
                return true
            }
        }
        return false
    }

    /**
     * Método protegido y abstracto, pensado para ser redefinido en las clases hijas. Define
     * el comportamiento de iniciar las actualizaciones de localización, según el tipo
     * de tracker que se esté utilizando.
     *
     * @param mode Modo de recepción de actualizaciones de localización.
     * @return true si se inician las actualizaciones con éxito.
     */
    protected abstract fun startLocationUpdates(mode: LocationUpdateMode): Boolean

    /**
     * Método protegido y abstracto que es redefinido en las clases hijas para
     * especificar el comportamiento adecuado para detener el tracker, en función del tipo.
     *
     * @param mode Modo de recepción de actualizaciones de localización.
     * @return true si se detienen las actualizaciones con éxito.
     */
    protected abstract fun stopLocationUpdates(mode: LocationUpdateMode): Boolean

    /**
     * Método protegido y abstracto que redefinen las clases hijas para devolver
     * el TAG en función del tipo de tracker.
     *
     * @return string con el TAG del tracker.
     */
    protected abstract fun getTrackerTag(): String
}