package es.uniovi.eii.contacttracker.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para el Fragmento de Comprobaciones
 * de Contactos de Riesgo.
 */
@HiltViewModel
class RiskContactViewModel @Inject constructor(
    private val locationRepository: LocationRepository
): ViewModel() {

    fun getItinerary() {
        viewModelScope.launch(Dispatchers.IO) {
            val it = locationRepository.getItinerarySince("2021-06-02")
        }
    }
}