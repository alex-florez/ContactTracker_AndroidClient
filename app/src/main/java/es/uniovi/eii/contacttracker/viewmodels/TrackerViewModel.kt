package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * View Model para el fragmento de rastreo de ubicación.
 */
@HiltViewModel
class TrackerViewModel @Inject constructor(): ViewModel() {

    /**
     * LiveData para el flag de servicio de localización.
     */
    private val _isLocationServiceActive = MutableLiveData(false)
    val isLocationServiceActive : LiveData<Boolean> = _isLocationServiceActive

    /**
     * LiveData para el flag de lista de localizaciones vacía.
     */
    private val _areLocationsAvailable = MutableLiveData(false)
    val areLocationsAvailable: LiveData<Boolean> = _areLocationsAvailable

    /**
     * Establece el estado del LiveData en función del
     * flag pasado como parámetro.
     */
    fun setIsLocationServiceActive(isActive: Boolean) {
        _isLocationServiceActive.value = isActive
    }

    /**
     * Establece el estado del flag de existencia de localizaciones
     * en el adapter.
     */
    fun setAreLocationsAvailable(areLocations: Boolean) {
        _areLocationsAvailable.value = areLocations
    }

    fun test(): Int {
        return 200
    }

}