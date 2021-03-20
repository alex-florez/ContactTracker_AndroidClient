package es.uniovi.eii.contacttracker.location.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import es.uniovi.eii.contacttracker.adapters.UserLocationAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentTrackerBinding
import es.uniovi.eii.contacttracker.fragments.tracklocation.TrackerInfoFragment
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.util.LocationUtils

/**
 * Broadcast Receiver que se dispara cuando se recibe
 * una nueva localización, para actualizar la UI.
 *
 * Recibe como parámetro en el constructor el ListAdapter de
 * Localizaciones de Usuario.
 */
class LocationUpdateBroadcastReceiver() : BroadcastReceiver() {

    /**
     * ListAdapter para las localizaciones de usuario, del Fragment de
     * información.
     */
    private lateinit var adapter: UserLocationAdapter

    /**
     * Referencia al Fragmento de Información de Log del rastreador.
     */
    private lateinit var infoFragment:TrackerInfoFragment

    constructor(adapter: UserLocationAdapter, infoFragment: TrackerInfoFragment) : this(){
        this.adapter = adapter
        this.infoFragment = infoFragment
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val location: Location? = intent?.getParcelableExtra(EXTRA_LOCATION)
        location?.let {
            Log.d(TAG, LocationUtils.format(it))
//            adapter.addUserLocation(LocationUtils.parse(location)) // Añadir la posición al adapter.
            infoFragment.userLocationAdapter.addUserLocation(LocationUtils.parse(location))
            infoFragment.toggleLabelNoLocations()
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