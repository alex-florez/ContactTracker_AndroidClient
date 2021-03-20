package es.uniovi.eii.contacttracker.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

/**
 * View Model para el fragment del Tracker (Rastreador de ubicación).
 */
class LocationHistoryViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    /**
     * Repository de localización.
     */
    private val locationRepository = LocationRepository(app)


    /**
     * LiveData de inserción
     */
    private val _insertedUserLocationId = MutableLiveData<Long>()
    val insertedUserLocationId: LiveData<Long> = _insertedUserLocationId

    /**
     * LiveData con el número de filas eliminadas
     */
    private val _affectedRows = MutableLiveData<Int>()
    val affectedRows: LiveData<Int> = _affectedRows

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
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        return locationRepository.getAllUserLocationsByDate(formatter.format(date))
    }

    /**
     * Invoca al repositorio para eliminar todas las localizaciones
     * del usuario de la base de datos.
     */
    fun deleteAllUserLocations(){
        viewModelScope.launch {
            _affectedRows.value = locationRepository.deleteAllUserLocations()
        }
    }

}