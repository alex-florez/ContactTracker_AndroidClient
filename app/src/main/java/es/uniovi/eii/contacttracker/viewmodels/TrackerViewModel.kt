package es.uniovi.eii.contacttracker.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.repositories.LocationRepository

/**
 * View Model para el fragment del Tracker (Rastreador de ubicación).
 */
class TrackerViewModel : ViewModel() {

    /**
     * LiveData para las localizaciones.
     */
    private val _locations = MutableLiveData<List<UserLocation>>()
    val locations: LiveData<List<UserLocation>> = _locations

    /**
     * Repository
     */
    private val locationRepository = LocationRepository()

    /**
     * Invoca al repositorio de localizción para activar o desactivar
     * el servicio de rastreo de ubicación.
     */
    fun toggleLocationService(ctx: Context, action: String){
        locationRepository.toggleLocationService(ctx, action)
    }
}