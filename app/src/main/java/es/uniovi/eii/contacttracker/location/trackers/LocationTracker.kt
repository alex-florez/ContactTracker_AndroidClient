package es.uniovi.eii.contacttracker.location.trackers

import android.app.PendingIntent
import es.uniovi.eii.contacttracker.location.LocationTrackRequest
import es.uniovi.eii.contacttracker.location.LocationUpdateMode
import es.uniovi.eii.contacttracker.location.listeners.callbacks.LocationUpdateCallback

/**
 * Interfaz que representa la abstracción del rastreador de ubicación.
 */
interface LocationTracker {

    /**
     * Inicia las actualizaciones de localización.
     *
     * @param mode modo de recepción de actualizaciones de localización.
     * @return true si se ha iniciado con éxito.
     */
    fun start(mode: LocationUpdateMode): Boolean

    /**
     * Detiene las actualizaciones de localización.
     *
     * @param mode modo de recepción de actualizaciones
     * @return true si se ha detenido con éxito.
     */
    fun stop(mode: LocationUpdateMode): Boolean

    /**
     * Establece una nueva petición de ubicación.
     *
     * @param request petición de ubicación
     */
    fun setLocationRequest(request: LocationTrackRequest)

    /**
     * Establece el callback de llamada que será invocado cada
     * vez que se obtiene una nueva actualización de localización.
     *
     * @param callback callback para las actualizaciones de localización.
     */
    fun setCallback(callback: LocationUpdateCallback)


    /**
     * Establece el callback de llamada de tipo IntentService, que será
     * invocado a través de un PendingIntent cada vez que se reciba una nueva
     * actualización de localización.
     *
     * @param pendingIntent Pending Intent para recibir las actualizaciones.
      */
    fun setIntentCallback(pendingIntent: PendingIntent)

    /**
     * Establece el tipo de provider que se utilizará para obtener
     * los datos de localización.
     *
     * @param provider fuente de localización.
     */
    fun setLocationProvider(provider: String)


}