package es.uniovi.eii.contacttracker.location.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import es.uniovi.eii.contacttracker.databinding.FragmentTrackLocationBinding
import es.uniovi.eii.contacttracker.util.LocationUtils

/**
 * Broadcast Receiver que dispara cuando se recibe
 * una nueva localización, para actualizar la UI.
 *
 * Recibe como parámetro en el constructor, el objeto binding
 * para actualizar la UI.
 */
class LocationUpdateBroadcastReceiver(
    private val binding: FragmentTrackLocationBinding
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val location: Location? = intent?.getParcelableExtra(EXTRA_LOCATION)
        location?.let {
            Log.d(TAG, LocationUtils.format(it))
            binding.txtLocationResult.text = LocationUtils.format(it)
        }
    }

    companion object {
        private const val TAG = "LocationUpdateBroadcastReceiver"

        /**
         * String que representa la acción por la que se filrarán
         * los broadcast que recibe este Receiver.
         */
        const val ACTION_GET_LOCATION = "Get Location"

        /**
         * EXTRAS
         */
        const val EXTRA_LOCATION = "locationExtra"
    }

}