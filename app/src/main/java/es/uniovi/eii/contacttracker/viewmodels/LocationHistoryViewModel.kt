package es.uniovi.eii.contacttracker.viewmodels

import android.app.Application
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.App
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.util.SingleLiveEvent
import es.uniovi.eii.contacttracker.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

/**
 * View Model para el fragment del Tracker (Rastreador de ubicación).
 */
@HiltViewModel
class LocationHistoryViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    /**
     * LiveData con el número de filas eliminadas
     */
    private val _deletedRows = SingleLiveEvent<Int>()
    val deletedRows: SingleLiveEvent<Int> = _deletedRows

    /**
     * Filtro de fecha Mutable.
     */
    val dateFilter = MutableLiveData<Date>()

    /**
     * Recibe como parámetro una fecha Date y la transforma
     * en String con el formato adecuado para invocar al
     * mètodo del repositorio.
     *
     * @param date fecha por la cual filtrar.
     * @return LiveData con las localizaciones.
     */
    fun getAllUserLocationsByDate(date: Date): LiveData<List<UserLocation>>{
        val formattedDate = Utils.formatDate(date, "yyyy-MM-dd")
        return locationRepository.getAllUserLocationsByDate(formattedDate)
    }

    /**
     * Utiliza el repositorio para eliminar las localizaciones del usuario
     * cuya fecha coincide con la pasada como parámetro.
     *
     * @param date fecha por la cual filtrar.
     */
    fun deleteUserLocationsByDate(date: Date) {
        val formattedDate = Utils.formatDate(date, "yyyy-MM-dd")
        viewModelScope.launch {
            _deletedRows.value = locationRepository.deleteUserLocationsByDate(formattedDate)
        }
    }

}