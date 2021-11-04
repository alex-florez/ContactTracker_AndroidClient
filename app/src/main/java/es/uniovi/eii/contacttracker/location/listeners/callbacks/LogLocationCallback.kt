package es.uniovi.eii.contacttracker.location.listeners.callbacks

import android.location.Location
import android.util.Log
import es.uniovi.eii.contacttracker.util.LocationUtils

/**
 * Implementación de la interfaz LocationUpdateCallback para
 * redefinir el comportamiento al recibir actualizaciones de localización.
 *
 * En este caso, se recibe una localización y se imprime sus datos en el Log.
 */
class LogLocationCallback : LocationUpdateCallback {

    override fun onLocationUpdate(location: Location) {
        val locationString = LocationUtils.format(location)
        Log.d(TAG, locationString)
    }

    override fun onLocationStop() {

    }

    companion object {
        private const val TAG: String = "LogLocationCallback"
    }
}