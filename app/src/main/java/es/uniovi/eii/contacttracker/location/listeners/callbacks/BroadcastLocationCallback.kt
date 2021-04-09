package es.uniovi.eii.contacttracker.location.listeners.callbacks

import android.content.Context
import android.content.Intent
import android.location.Location
import android.provider.SyncStateContract
import es.uniovi.eii.contacttracker.Constants

/**
 * Clase que implementa la interfaz de callback para recibir actualizaciones de
 * localización y realizar un broadcast con los datos de localización. Este
 * broadcast será recibido por aquellos componentes que lo registren.
 */
class BroadcastLocationCallback(
    private val ctx: Context
) : LocationUpdateCallback {

    override fun onLocationUpdate(location: Location) {
        val intent = Intent()
        intent.action = Constants.ACTION_GET_LOCATION
        intent.putExtra(Constants.EXTRA_LOCATION, location)
        ctx.sendBroadcast(intent)
    }

    override fun onLocationStop() {

    }
}