package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.di.IoDispatcher
import es.uniovi.eii.contacttracker.fragments.riskcontacts.CheckMode
import es.uniovi.eii.contacttracker.model.Error
import es.uniovi.eii.contacttracker.repositories.RiskContactRepository
import es.uniovi.eii.contacttracker.riskcontact.RiskContactManager
import es.uniovi.eii.contacttracker.riskcontact.alarms.MAX_ALARM_COUNT
import es.uniovi.eii.contacttracker.riskcontact.alarms.RiskContactAlarm
import es.uniovi.eii.contacttracker.riskcontact.alarms.RiskContactAlarmManager
import es.uniovi.eii.contacttracker.util.AndroidUtils
import es.uniovi.eii.contacttracker.util.ValueWrapper
import kotlinx.coroutines.CoroutineDispatcher
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
    private val riskContactAlarmManager: RiskContactAlarmManager,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
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
     * Contiene la alarma de comprobación establecida con éxito.
     */
    private val _addAlarmSuccess = MutableLiveData<RiskContactAlarm>()
    val addAlarmSuccess: LiveData<RiskContactAlarm> = _addAlarmSuccess

    /**
     * Contiene el código del String del error determinado
     * al insertar una nueva alarma de comprobación.
     */
    private val _addAlarmError = MutableLiveData<Int>()
    val addAlarmError: LiveData<Int> = _addAlarmError

    /* Contiene el código del String del error de superar el máximo
    * de alarmas de comprobación.
    */
    private val _alarmLimitError = MutableLiveData<Int>()
    val alarmLimitError: LiveData<Int> = _alarmLimitError


    /**
     * Listado inicial con las alarmas de comprobación establecidas.
     */
    private val _alarms = MutableLiveData<List<RiskContactAlarm>>()
    val alarms: LiveData<List<RiskContactAlarm>> = _alarms

    /**
     * Etiqueta utilizada cuando no hay alarmas de comprobación.
     */
    private val _emptyAlarms = MediatorLiveData<Boolean>()
    val emptyAlarms: MediatorLiveData<Boolean> = _emptyAlarms

    // Constructor
    init {
        emptyAlarms.addSource(alarms) {
            _emptyAlarms.value = it.isEmpty()
        }
    }

    /**
     * Hace uso del manager de contactos de riesgo para
     * comenzar una nueva comprobación en la fecha pasada como parámetro.
     *
     * @param date Fecha objetivo en la que se realiza la comprobación.
     */
   fun startChecking(date: Date) {
       viewModelScope.launch(dispatcher) {
           _isChecking.postValue(true)
           riskContactManager.checkRiskContacts(date)
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
        viewModelScope.launch(dispatcher) {
            when(val result = riskContactAlarmManager.set(RiskContactAlarm(null, date,true))) {
                is ValueWrapper.Success -> {
                    _addAlarmSuccess.postValue(result.value)
                }
                is ValueWrapper.Fail -> {
                    processError(result.error)
                }
            }
        }
    }

    /**
     * Elimina la alarma de comprobación cuyo ID coincide con el ID pasado como parámetro.
     *
     * @param alarmID ID de la alarma de comprobación a eliminar.
     */
    fun removeAlarm(alarmID: Long) {
        viewModelScope.launch(dispatcher) {
            riskContactAlarmManager.remove(alarmID)
        }
    }

    /**
     * Activa o desactiva las alarmas de comprobación actualmente existentes.
     *
     * @param activate Flag para activar o desactivar las alarmas de comprobación.
     */
    fun toggleCheckAlarms(activate: Boolean) {
        viewModelScope.launch(dispatcher) {
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
        }
    }

    /**
     * Actualiza el valor de LiveData que contiene el flag para
     * la etiqueta de lista de alarmas vacía.
     */
    fun setLabelNoAlarms(value: Boolean) {
        _emptyAlarms.value = value
    }

    /**
     * Rellena los LiveDatas con los códigos de error en función del
     * error determinado de insertar una alarma de comprobación.
     */
    private fun processError(error: Error) {
        when(error) {
            Error.RISK_CONTACT_ALARM_COLLISION -> {
                _addAlarmError.postValue(R.string.checkAlarmErrorCollision)
            }
            Error.RISK_CONTACT_ALARM_COUNT_LIMIT_EXCEEDED ->{
                _alarmLimitError.postValue(R.string.checkAlarmErrorCountLimit)
            }
            else -> {
                _addAlarmError.postValue(R.string.genericError)
            }
        }
    }
}