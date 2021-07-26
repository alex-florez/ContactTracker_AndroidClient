package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.util.SingleLiveEvent
import es.uniovi.eii.contacttracker.util.DateUtils
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * View Model para el fragment del histórico de localizaciones.
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
    private val _dateFilter = MutableLiveData(Date())
    val dateFilter: LiveData<Date> = _dateFilter

    /**
     * LiveData para las localizaciones con una transformación a través del
     * filtro de fecha.
     */
    val locations: LiveData<List<UserLocation>> = Transformations.switchMap(dateFilter) {
        getAllUserLocationsByDate(it)
    }

    /**
     * Etiqueta de lista de localizaciones vacía.
     */
    private val _noLocations = MediatorLiveData<Boolean>()
    val noLocations: MediatorLiveData<Boolean> = _noLocations

    /**
     * LiveData para el número de localizaciones.
     */
    private val _numberOfLocations = MediatorLiveData<Int>()
    val numberOfLocations: MediatorLiveData<Int> = _numberOfLocations

    /**
     * Recibe como parámetro una fecha Date y la transforma
     * en String con el formato adecuado para invocar al
     * mètodo del repositorio.
     *
     * @param date fecha por la cual filtrar.
     * @return LiveData con las localizaciones.
     */
    private fun getAllUserLocationsByDate(date: Date): LiveData<List<UserLocation>>{
        val formattedDate = DateUtils.formatDate(date, "yyyy-MM-dd")
        return locationRepository.getAllUserLocationsByDate(formattedDate)
    }

    /**
     * Utiliza el repositorio para eliminar las localizaciones del usuario
     * cuya fecha coincide con la pasada como parámetro.
     *
     * @param date fecha por la cual filtrar.
     */
    fun deleteUserLocationsByDate(date: Date) {
        val formattedDate = DateUtils.formatDate(date, "yyyy-MM-dd")
        viewModelScope.launch {
            _deletedRows.value = locationRepository.deleteUserLocationsByDate(formattedDate)
        }
    }

    /**
     * Actualiza el valor del MutableLiveData para el filtro de fecha
     * con la nueva fecha pasada como parámetro.
     *
     * @param date Nueva fecha.
     */
    fun setDateFilter(date: Date) {
        _dateFilter.value = date
    }

    init {
        // Agregar fuente para la etiqueta de lista vacía.
        noLocations.addSource(locations) {
            _noLocations.value = it.isEmpty()
        }
        // Fuente para el número de localizaciones
        numberOfLocations.addSource(locations) {
            _numberOfLocations.value = it.size
        }
    }

}