package es.uniovi.eii.contacttracker.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.model.*
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.repositories.RiskContactRepository
import es.uniovi.eii.contacttracker.riskcontact.RiskContactManager
import es.uniovi.eii.contacttracker.riskcontact.detector.RiskContactDetector
import es.uniovi.eii.contacttracker.riskcontact.detector.RiskContactDetectorImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import javax.inject.Inject

/**
 * ViewModel para el Fragmento de Comprobaciones
 * de Contactos de Riesgo.
 */
@HiltViewModel
class RiskContactViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val riskContactManager: RiskContactManager,
    private val riskContactRepository: RiskContactRepository
): ViewModel() {

    /**
     * LiveData que indica si se está ejecutando la comprobación.
     */
    private val _isDetecting = MutableLiveData(false)
    val isDetecting: LiveData<Boolean> = _isDetecting

   fun detect() {
       viewModelScope.launch(Dispatchers.IO) {
           _isDetecting.postValue(true)
           riskContactManager.checkRiskContacts()
           val result = riskContactRepository.getAll()
           _isDetecting.postValue(false)
           Log.d("PRUEBISIMA", result.size.toString())
       }
   }


}