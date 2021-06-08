package es.uniovi.eii.contacttracker.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.model.Itinerary
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.riskcontact.detector.RiskContactDetector
import es.uniovi.eii.contacttracker.riskcontact.detector.RiskContactDetectorImpl
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

    /**
     * RiskContact Detector
     */
    private val detector: RiskContactDetector = RiskContactDetectorImpl()

   fun detect() {
       detector.startChecking(Itinerary(mapOf()), Itinerary(mapOf()))
   }
}