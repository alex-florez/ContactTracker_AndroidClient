package es.uniovi.eii.contacttracker.location.listeners.intents

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.LocationResult
import es.uniovi.eii.contacttracker.util.LocationUtils

/**
 * Intent Service que actúa de listener para recibir
 * las actualizaciones de localización mediante un PendingIntent.
 */
class LocationReceivedIntentService : IntentService("LocationReceivedIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        val locationResult = LocationResult.extractResult(intent)
        locationResult?.let {
            val location = it.lastLocation
            Log.d(TAG, LocationUtils.format(location))
        }
    }

    companion object {
        private const val TAG = "LocationReceivedIntentService"
    }
}