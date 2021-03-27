package es.uniovi.eii.contacttracker.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.repositories.AlarmRepository
import es.uniovi.eii.contacttracker.util.Utils
import javax.inject.Inject
import java.util.Date

@HiltViewModel
class TrackerViewModel @Inject constructor(
        private val alarmRepository: AlarmRepository
) : ViewModel() {

    /**
     * Hora de INICIO
     */
    private val _startTime = MutableLiveData<Date>()
    val starTime: LiveData<Date> = _startTime

    /**
     * Hora de FIN
     */
    private val _endTime = MutableLiveData<Date>()
    val endTime: LiveData<Date> = _endTime

    /**
     * Alarma ACTUAL
     */
    private val _actualAlarm = MutableLiveData<String>()
    val actualAlarm: LiveData<String> = _actualAlarm

    /**
     * LABEL de alarma actual
     */
    private val _labelActualAlarm = MutableLiveData("Alarma pendiente")
    val labelActualAlarm: LiveData<String> = _labelActualAlarm

    // SETTERS
    fun setStartTime(date: Date){
        _startTime.value = date
    }

    fun setEndTime(date: Date) {
        _endTime.value = date
    }


    /**
     * Almacena las horas seleccionadas desde la UI en
     * las SharedPreferences.
     */
    fun saveAlarmsToSharedPrefs() {
        _startTime.value?.let { start ->
            _endTime.value?.let{ end ->
                alarmRepository.setLocationAlarmTime(start, R.string.shared_prefs_location_alarm_start)
                alarmRepository.setLocationAlarmTime(end, R.string.shared_prefs_location_alarm_end)
            }
        }
    }

    /**
     * Elimina las alarmas de las SharedPreferences.
     */
    fun removeAlarmsFromSharedPrefs(){
        alarmRepository.remove(R.string.shared_prefs_location_alarm_start)
        alarmRepository.remove(R.string.shared_prefs_location_alarm_end)
    }

    /**
     * Recupera de las sharedpreferecnes las horas de inicio y de fi
     * de la alarma actual, y crea el String formateado correspondiente.
     */
    fun retrieveActualAlarm(){
        // Comprobar si existe
        if(alarmRepository.checkKey(R.string.shared_prefs_location_alarm_start)
                && alarmRepository.checkKey(R.string.shared_prefs_location_alarm_end)){
            val actualStart = alarmRepository.getLocationAlarmTime(R.string.shared_prefs_location_alarm_start)
            val actualEnd = alarmRepository.getLocationAlarmTime(R.string.shared_prefs_location_alarm_end)
            _actualAlarm.value = "(${Utils.formatDate(actualStart, "HH:mm")}) " +
                    "- (${Utils.formatDate(actualEnd, "HH:mm")})"
            _labelActualAlarm.value = "Alarma pendiente"
        } else {
            _actualAlarm.value = ""
            _labelActualAlarm.value = "No hay alarmas programadas"
        }
    }


}