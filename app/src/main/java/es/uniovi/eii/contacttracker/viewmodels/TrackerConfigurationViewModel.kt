package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.repositories.TrackerSettingsRepository
import es.uniovi.eii.contacttracker.util.DateUtils
import javax.inject.Inject

/**
 * View Model para el Fragment de configuración ddel Tracker.
 */
@HiltViewModel
class TrackerConfigurationViewModel @Inject constructor(
        private val trackerSettingsRepository: TrackerSettingsRepository
) : ViewModel() {

    // PARÁMETROS DE CONFIGURACIÓN

    /**
     * MinInterval: Intervalo de tiempo mínimo.
     */
    private val _minInterval = MutableLiveData<Long>()
    val minInterval: LiveData<Long> = _minInterval

    /**
     * SmallestDisplacement: Desplazamiento mínimo.
     */
    private val _smallestDisplacement = MutableLiveData<Float>()
    val smallestDisplacement: LiveData<Float> = _smallestDisplacement

    /**
     * Invoca al repositorio de configuración de localización
     * para obtener el nº de segundos de intervalo de tiempo mínimo.
     */
    fun getMinInterval() {
        _minInterval.value = trackerSettingsRepository.getMinInterval()
    }

    /**
     * Devuelve el valor actual del intervalo mínimo transformado
     * a minutos y segundos.
     */
    fun getMinIntervalMinSecs(): Array<Int> {
        _minInterval.value?.let{ return DateUtils.getMinuteSecond(it) }
        return arrayOf()
    }

    /**
     * Actualiza el intervalo de tiempo mínimo con los
     * milisegundos pasados como parámetro, y modifica el valor
     * del LiveData para mantenerlo actualizado.
     *
     * @param seconds segundos del intervalo de tiempo.
     */
    fun updateMinInterval(millis: Long) {
        trackerSettingsRepository.setMinInterval(millis)
        getMinInterval() // Actualizar LiveData
    }

    /**
     * Actualiza el valor del LiveData con el
     * valor del desplazamiento mínimo almacenado en las
     * SharedPrefs.
     */
    fun getSmallestDisplacement(){
        _smallestDisplacement.value = trackerSettingsRepository.getSmallestDisplacement()
    }

    /**
     * Actualiza el parámetro del desplazamiento mínimo almacenado
     * en las SharedPreferences con el nuevo valor pasado como parámetro.
     *
     * @param meters metros de desplazamiento.
     */
    fun updateSmallestDisplacement(meters: Float) {
        trackerSettingsRepository.setSmallestDisplacement(meters)
        getSmallestDisplacement() // Actualizar LiveData
    }
}
