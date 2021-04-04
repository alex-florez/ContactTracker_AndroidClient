package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.location.alarms.LocationAlarmManager
import es.uniovi.eii.contacttracker.model.LocationAlarmData
import es.uniovi.eii.contacttracker.repositories.AlarmRepository
import es.uniovi.eii.contacttracker.util.Utils
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
     * Flag de validez de las horas de la alarma.
     */
    private val _flagValidHours = MutableLiveData(true)
    val flagValidHours: LiveData<Boolean> = _flagValidHours

    /**
     * Location Alarm Manager
     */
    @Inject lateinit var locationAlarmManager:LocationAlarmManager


    // SETTERS para los placeholders de hora de INICIO y FIN.
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
     * Método encargado de establecer la alarma de localización, con
     * las horas de inicio y fin indicadas en los LiveData. Devuelve True
     * si la alarma se ha establecido correctamente.
     *
     * @return true si hay éxito.
     */
    fun setLocationAlarm(): Boolean{
        _startTime.value?.let { start->
            _endTime.value?.let { end ->
                if(checkHours(start, end)){ // Comprobar validez de horas.
                    val locationAlarmData = getAlarmTime(LocationAlarmData(start, end))
                    saveAlarmsToSharedPrefs(locationAlarmData.startDate, locationAlarmData.endDate) // Guardar en las SharedPrefs
                    _actualAlarm.value?.let {
                        locationAlarmManager.set(locationAlarmData) // Establecer alarma.
                        return true
                    }
                }
            }
        }
        return false
    }


    /**
     * Activa o desactiva el rastreo automático.
     */
    fun toggleAutoTracking(autoTracking: Boolean){
        alarmRepository.setAutoTracking(autoTracking)
        if(autoTracking) { // Restaurar alarmas
            retrieveActualAlarm()
            _actualAlarm.value?.let {
                val locationAlarmData = getAlarmTime(it)
                saveAlarmsToSharedPrefs(locationAlarmData.startDate, locationAlarmData.endDate)
                locationAlarmManager.set(locationAlarmData)
            }
        } else { // Cancelar alarmas
            locationAlarmManager.cancel()
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
     * Devuelve true si el rastreo automático está
     * habilitado.
     *
     * @return true o false según el estado del rastreo automático.
     */
    fun isAutoTrackingEnabled(): Boolean {
        return alarmRepository.getAutoTracking()
    }

    /**
     * Método que realiza las comprobaciones necesarias
     * para verificar la validez de las horas de inicio
     * y fin de alarma.
     */
    private fun checkHours(start: Date, end: Date): Boolean {
//        val startHours = Utils.getFromDate(start, Calendar.HOUR_OF_DAY)
//        val startMinutes = Utils.getFromDate(start, Calendar.MINUTE)
//        val endHours = Utils.getFromDate(end, Calendar.HOUR_OF_DAY)
//        val endMinutes = Utils.getFromDate(end, Calendar.MINUTE)
//        val startDate = Utils.getDate(startHours, startMinutes)
//        val endDate = Utils.getDate(endHours, endMinutes)
        return if(start.before(end)){
            _flagValidHours.value = true
            true
        } else {
            _flagValidHours.value = false
            false
        }
    }

    /**
     * Devuelve un LocationAlarmData procesado con la fecha de inicio y de fin para la
     * alarma de localización, sumándole un día a cada fecha en el caso
     * de que sean anteriores a la fecha actual.
     */
    private fun getAlarmTime(alarmData: LocationAlarmData): LocationAlarmData {
        return if(alarmData.startDate.before(Date())){
            LocationAlarmData(Utils.addToDate(alarmData.startDate, Calendar.DATE, 1),
                    Utils.addToDate(alarmData.endDate, Calendar.DATE, 1))
        } else {
           alarmData
        }
    }

    /**
     * Almacena las horas seleccionadas desde la UI en
     * las SharedPreferences, y actualiza el valor de los LiveData
     * con las nuevas horas añadidas.
     */
    private fun saveAlarmsToSharedPrefs(startTime: Date, endTime: Date) {
        alarmRepository.setLocationAlarmTime(startTime, R.string.shared_prefs_location_alarm_start)
        alarmRepository.setLocationAlarmTime(endTime, R.string.shared_prefs_location_alarm_end)
        retrieveActualAlarm() // Actualizar LiveData.
    }

}