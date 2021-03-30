package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.model.LocationAlarmData
import es.uniovi.eii.contacttracker.repositories.AlarmRepository
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor(
        private val alarmRepository: AlarmRepository
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
     * Alarma ACTUAL almacenada en las SharedPreferences.
     */
    private val _actualAlarm = MutableLiveData<LocationAlarmData?>()
    val actualAlarmData: LiveData<LocationAlarmData?> = _actualAlarm


    /**
     * Flag para habilitar/deshabilitar el rastreo automático.
     */
    private val _flagAutoTracking = MutableLiveData(false)
    val flagAutoTracking: LiveData<Boolean> = _flagAutoTracking

    // SETTERS para los placeholders.
    fun setStartTime(date: Date){
        _startTime.value = date
    }

    fun setEndTime(date: Date) {
        _endTime.value = date
    }

    /**
     * Establece los valores iniciales para las horas
     * de inicio y de fin de la alarma de localización,
     * a modo de PlaceHolder.
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
     * Almacena las horas seleccionadas desde la UI en
     * las SharedPreferences, y actualiza el valor de los LiveData
     * con las nuevas horas añadidas.
     */
    fun saveAlarmsToSharedPrefs() {
        _startTime.value?.let { start ->
            _endTime.value?.let{ end ->
                alarmRepository.setLocationAlarmTime(start, R.string.shared_prefs_location_alarm_start)
                alarmRepository.setLocationAlarmTime(end, R.string.shared_prefs_location_alarm_end)
                retrieveActualAlarm() // Actualizar LiveData.
            }
        }
    }

    /**
     * Recupera de las SharedPreferences las horas de inicio y de fin
     * de la alarma actual y actualiza los LiveData.
     */
    fun retrieveActualAlarm(){
        // Comprobar si existe
        if(alarmRepository.checkKey(R.string.shared_prefs_location_alarm_start) && alarmRepository.checkKey(R.string.shared_prefs_location_alarm_end)){
            val startDate = alarmRepository.getLocationAlarmTime(R.string.shared_prefs_location_alarm_start)
            val endDate = alarmRepository.getLocationAlarmTime(R.string.shared_prefs_location_alarm_end)
            _actualAlarm.value = LocationAlarmData(startDate, endDate)
        } else {
            _actualAlarm.value = null
        }
    }

    /**
     * Elimina las alarmas de las SharedPreferences.
     */
    fun removeAlarmsFromSharedPrefs(){
        alarmRepository.remove(R.string.shared_prefs_location_alarm_start)
        alarmRepository.remove(R.string.shared_prefs_location_alarm_end)
        alarmRepository.remove(R.string.shared_prefs_location_alarm_enabled)
    }

    /**
     * Activa o desactiva el rastreo automático.
     */
    fun toggleAutoTracking(autoTracking: Boolean){
        alarmRepository.setAutoTracking(autoTracking)
        _flagAutoTracking.value = autoTracking // Actualizar LiveData
    }

    /**
     * Devuelve true si el rastreo automático está
     * habilitado.
     *
     * @return true o false según el estado del rastreo automático.
     */
    fun isAutoTrackingEnabled(): Boolean {
        return alarmRepository.getAutoTracking()
    }
}