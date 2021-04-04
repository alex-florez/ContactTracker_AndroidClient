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
     * LiveData de inserción
     */
    private val _insertedUserLocationId = MutableLiveData<Long>()
    val insertedUserLocationId: LiveData<Long> = _insertedUserLocationId

    /**
     * LiveData con el número de filas eliminadas
     */
    private val _deletedRows = MutableLiveData<Int>()
    val deletedRows: LiveData<Int> = _deletedRows

    /**
     * Filtro de fecha Mutable.
     */
    val dateFilter = MutableLiveData<Date>()

    /**
     * Hace uso del repositorio para insertar en la base de datos el
     * objeto UserLocation pasado como parámetro.
     *
     * @param userLocation localización del usuario.
     */
    fun insertUserLocation(userLocation: UserLocation){
        viewModelScope.launch {
            _insertedUserLocationId.value = locationRepository.insertUserLocation(userLocation)
        }
    }

    /**
     * Método que hace uso del Repositorio para inyectar y popular
     * los datos de todas las localizaciones registradas del usuario
     * en el Objeto LiveData correspondiente.
     *
     * @return livedata con la lista de localizaciones.
     */
    fun getAllUserLocations(): LiveData<List<UserLocation>> {
        return locationRepository.getAllUserLocations()
    }

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
     * Invoca al repositorio para eliminar todas las localizaciones
     * del usuario de la base de datos.
     */
    fun deleteAllUserLocations(){
        viewModelScope.launch {
            _deletedRows.value = locationRepository.deleteAllUserLocations()
        }
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