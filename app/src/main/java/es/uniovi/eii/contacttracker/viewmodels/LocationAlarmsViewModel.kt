package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.location.alarms.LocationAlarmManager
import es.uniovi.eii.contacttracker.model.LocationAlarm
import es.uniovi.eii.contacttracker.util.ValueWrapper
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * ViewModel para el Fragmento de gestión de alarmas de localización.
 */
@HiltViewModel
class LocationAlarmsViewModel @Inject constructor(
        private val locationAlarmManager: LocationAlarmManager
) : ViewModel() {

    /**
     * Placeholder Hora de INICIO
     */
    private val _startTime = MutableLiveData<Date>()
    val starTime: LiveData<Date> = _startTime

    /**
     * Placeholder Hora de FIN
     */
    private val _endTime = MutableLiveData<Date>()
    val endTime: LiveData<Date> = _endTime

    /**
     * Resultado de programar una alarma.
     */
    private val _alarmSetResult = MutableLiveData<ValueWrapper<Unit>>()
    val alarmSetResult: LiveData<ValueWrapper<Unit>> = _alarmSetResult

    // SETTERS para los placeholders de hora de INICIO y FIN.
    fun setStartTime(date: Date){
        _startTime.value = date
    }

    fun setEndTime(date: Date) {
        _endTime.value = date
    }

    /**
     * Establece los valores iniciales para las horas de inicio y de fin
     * de la alarma de localización, a modo de PlaceHolder.
     */
    fun initAlarmPlaceHolders(){
        val actualDate = Date()
        val cal = Calendar.getInstance()
        cal.time = actualDate
        cal.add(Calendar.HOUR_OF_DAY, 1) // Sumar una hora
        _startTime.value = actualDate
        _endTime.value = cal.time
    }

    /**
     * Devuelve un LiveData con la lista de todas las
     * alarmas de localización programadas por el usuario.
     */
    fun getAllAlarms(): LiveData<List<LocationAlarm>> {
        return locationAlarmManager.getAllAlarms()
    }

    /**
     * Crea e inserta una nueva alarma a partir de las horas de inicio y
     * de fin seleccionadas desde la interfaz de usuario.
     */
    fun addNewAlarm() {
        // Comprobar valores no nulos.
        val startDate = _startTime.value ?: return
        val endDate = _endTime.value ?: return
        val alarm = LocationAlarm( // Instancia de la Alarma de Localización
                null, // ID autogenerado por ROOM
                startDate,
                endDate,
                true // Activada por defecto
        )
        viewModelScope.launch {
            _alarmSetResult.value = locationAlarmManager.setAlarm(alarm)
        }
    }

    /**
     * Invoca al Manager para eliminar y cancelar la alarma
     * de localización pasada como parámetro.
     *
     * @param locationAlarm alarma de localización a eliminar.
     */
    fun deleteAlarm(locationAlarm: LocationAlarm) {
        viewModelScope.launch {
            locationAlarm.id?.let {
                locationAlarmManager.deleteAlarm(it)
            }
        }
    }

    /**
     * Activa o desactiva una alarma de localización pasada como
     * parámetro, según el flag indicado. Para ello, utiliza el
     * manager correspondiente a las alarmas de localización.
     *
     * @param locationAlarm Alarma de localización.
     * @param enable flag para modificar el estado de la alarma de localización.
     */
    fun toggleAlarmState(locationAlarm: LocationAlarm, enable: Boolean) {
        viewModelScope.launch {
            locationAlarm.id?.let {locationAlarmManager.toggleAlarm(it, enable)}
        }
    }

}