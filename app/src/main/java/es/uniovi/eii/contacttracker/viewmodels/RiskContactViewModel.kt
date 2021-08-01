package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.fragments.riskcontacts.CheckMode
import es.uniovi.eii.contacttracker.repositories.RiskContactRepository
import es.uniovi.eii.contacttracker.riskcontact.RiskContactManager
import es.uniovi.eii.contacttracker.riskcontact.alarms.RiskContactAlarm
import es.uniovi.eii.contacttracker.riskcontact.alarms.RiskContactAlarmManager
import es.uniovi.eii.contacttracker.util.ValueWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel para el Fragmento de Comprobaciones
 * de Contactos de Riesgo.
 */
@HiltViewModel
class RiskContactViewModel @Inject constructor(
    private val riskContactManager: RiskContactManager,
    private val riskContactAlarmManager: RiskContactAlarmManager
): ViewModel() {

    /**
     * LiveData que indica si se está ejecutando la comprobación.
     */
    private val _isChecking = MutableLiveData(false)
    val isChecking: LiveData<Boolean> = _isChecking

    /**
     * LiveData para la hora de la comprobación.
     */
    private val _checkHour = MutableLiveData(Date())
    val checkHour: LiveData<Date> = _checkHour

    /**
     * ValueWrapper con el resultado de añadir una alarma de comprobación.
     */
    private val _addAlarmResult = MutableLiveData<ValueWrapper<RiskContactAlarm>>()
    val addAlarmResult: LiveData<ValueWrapper<RiskContactAlarm>> = _addAlarmResult

    /**
     * Listado inicial con las alarmas de comprobación establecidas.
     */
    private val _alarms = MutableLiveData<List<RiskContactAlarm>>()
    val alarms: LiveData<List<RiskContactAlarm>> = _alarms

    /**
     * Etiqueta utilizada cuando no hay alarmas de comprobación.
     */
    private val _emptyAlarms = MutableLiveData(true)
    val emptyAlarms: LiveData<Boolean> = _emptyAlarms

    /**
     * Hace uso del manager de contactos de riesgo para
     * comenzar una nueva comprobación.
     */
   fun startChecking() {
       viewModelScope.launch(Dispatchers.IO) {
           _isChecking.postValue(true)
           delay(2000)
           riskContactManager.checkRiskContacts()
           _isChecking.postValue(false)
       }
   }

    /**
     * Actualiza la hora de la comprobación.
     *
     * @param date Nueva hora de comprobación.
     */
    fun setCheckHour(date: Date) {
        _checkHour.value = date
    }

    /**
     * Establece el modo de comprobación.
     *
     * @param checkMode Nuevo modo de comprobación.
     */
    fun setCheckMode(checkMode: CheckMode) {
        riskContactManager.setCheckMode(checkMode)
    }

    /**
     * Devuelve el modo de comprobación actual.
     *
     * @return Modo de comprobación actual.
     */
    fun getCheckMode(): CheckMode {
        return riskContactManager.getCheckMode()
    }

    /**
     * Invoca al manager de alarmas de contactos de riesgo para establecer una
     * nueva alarma de comprobación que se dispare en la fecha indicada.
     *
     * @param date Fecha en la que se debe ejecutar la alarma de comprobación.
     */
    fun addAlarm(date: Date){
        viewModelScope.launch {
            _addAlarmResult.value = riskContactAlarmManager.set(RiskContactAlarm(null, date, true))
        }
    }

    /**
     * Elimina la alarma de comprobación cuyo ID coincide con el ID pasado como parámetro.
     *
     * @param alarmID ID de la alarma de comprobación a eliminar.
     */
    fun removeAlarm(alarmID: Long) {
        viewModelScope.launch {
            riskContactAlarmManager.remove(alarmID)
        }
    }

    /**
     * Activa o desactiva las alarmas de comprobación actualmente existentes.
     *
     * @param activate Flag para activar o desactivar las alarmas de comprobación.
     */
    fun toggleCheckAlarms(activate: Boolean) {
        viewModelScope.launch {
            riskContactAlarmManager.toggle(activate)
        }
    }


    /**
     * Carga inicialmente todas las alarmas de comprobación
     * almacenadas en la base de datos.
     */
    fun loadAlarms() {
        viewModelScope.launch {
            _alarms.value = riskContactAlarmManager.getAllAlarms()
            _emptyAlarms.value = _alarms.value?.isEmpty() ?: true
        }
    }

    /**
     * Actualiza el valor de LiveData que contiene el flag para
     * la etiqueta de lista de alarmas vacía.
     */
    fun setLabelNoAlarms(value: Boolean) {
        _emptyAlarms.value = value
    }
}