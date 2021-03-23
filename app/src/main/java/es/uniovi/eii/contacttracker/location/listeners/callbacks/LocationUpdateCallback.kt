package es.uniovi.eii.contacttracker.location.listeners.callbacks

import android.location.Location

/**
 * Interfaz qeu modela un Callback que será invocado con cada
 * nueva actualización de localización.
 */
interface LocationUpdateCallback {

    /**
     * Método invocado cuando se recibe una nueva actualización
     * de localización. Recibe como parámetro los datos de la nueva
     * localización.
     * @param location nueva localización
     */
    fun onLocationUpdate(location: Location)

    /**
     * Método que será invocado cuando se dentenga el rastreador de ubicación,
     * y por tanto se pause el ForegroundService.
     */
    fun onLocationStop()
}