package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.location.alarms.LocationAlarmManager
import es.uniovi.eii.contacttracker.model.LocationAlarm
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
     * Crea e inserta una nueva alarma a partir de
     * las horas de inicio y de fin seleccionadas desde
     * el Fragment.
     */
    fun addNewAlarm() {
//        locationAlarmManager.deleteAllAlarms()
        val startDate = _startTime.value ?: return
        val endDate = _endTime.value ?: return
        val alarm = LocationAlarm(
                null,
                startDate,
                endDate,
                true
        )
        locationAlarmManager.setAlarm(alarm)
    }

    /**
     * Invoca al Manager para eliminar y cancelar la alarma
     * de ID pasado como parámetro.
     *
     * @param alarmID ID de la alarma a eliminar.
     */
    fun deleteAlarm(alarmID: Long) {
        locationAlarmManager.deleteAlarm(alarmID)
    }

}