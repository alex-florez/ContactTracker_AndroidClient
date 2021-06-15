package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.fragments.riskcontacts.CheckMode
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.repositories.RiskContactRepository
import es.uniovi.eii.contacttracker.riskcontact.RiskContactManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
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
    private val _isChecking = MutableLiveData(false)
    val isChecking: LiveData<Boolean> = _isChecking

    /**
     * LiveData para la hora de la comprobación.
     */
    private val _checkHour = MutableLiveData<Date>()
    val checkHour: LiveData<Date> = _checkHour

    /**
     * Hace uso del manager de contactos de riesgo para
     * comenzar una nueva comprobación.
     */
   fun startChecking() {
       viewModelScope.launch(Dispatchers.IO) {
           _isChecking.postValue(true)
           riskContactManager.checkRiskContacts()
           val result = riskContactRepository.getAll()
           _isChecking.postValue(false)
       }
   }

    /**
     * Establece el modo de comprobación.
     *
     * @param checkMode Nuevo modo de comprobación.
     */
    fun setCheckMode(checkMode: CheckMode) {
        riskContactRepository.setCheckMode(checkMode)
    }

    /**
     * Devuelve el modo de comprobación actual.
     *
     * @return Modo de comprobación actual.
     */
    fun getCheckMode(): CheckMode {
        return riskContactRepository.getCheckMode()
    }

    /**
     * Actualiza la hora de la comprobación.
     *
     * @param date Nueva hora de comprobación.
     */
    fun setCheckHour(date: Date) {
        // Actualizar livedata
        _checkHour.value = date
        // Guardar en las SharedPrefs
        riskContactRepository.setCheckHour(date)
    }

    /**
     * Devuelve la fecha con la hora establecida para
     * realizar la comprobación de contactos de riesgo.
     *
     * @return Fecha con la hora de la comprobación.
     */
    fun getCheckHour(): Date {
        return riskContactRepository.getCheckHour()
    }

}