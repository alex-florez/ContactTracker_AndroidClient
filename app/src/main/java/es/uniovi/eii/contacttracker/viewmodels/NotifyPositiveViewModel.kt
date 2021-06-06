package es.uniovi.eii.contacttracker.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.model.PersonalData
import es.uniovi.eii.contacttracker.model.NotifyPositiveResult
import es.uniovi.eii.contacttracker.model.Positive
import es.uniovi.eii.contacttracker.network.model.ResultWrapper
import es.uniovi.eii.contacttracker.repositories.ConfigRepository
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.repositories.PersonalDataRepository
import es.uniovi.eii.contacttracker.repositories.PositiveRepository
import es.uniovi.eii.contacttracker.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * ViewModel para el Fragmento de Notificar un positivo
 * y subir las coordenadas.
 */
@HiltViewModel
class NotifyPositiveViewModel @Inject constructor(
    private val positiveRepository: PositiveRepository,
    private val locationRepository: LocationRepository,
    private val personalDataRepository: PersonalDataRepository,
    private val configRepository: ConfigRepository
) : ViewModel() {

    /**
     * Resultado exitoso de NOTIFICAR un POSITIVO.
     */
    private val _notifyPositiveResult = MutableLiveData<NotifyPositiveResult>()
    val notifyPositiveResult: LiveData<NotifyPositiveResult> = _notifyPositiveResult

    /**
     * Flag para adjuntar los datos personales.
     */
    private val _flagAddPersonalData = MutableLiveData(false)
    val flagAddPersonalData: LiveData<Boolean> = _flagAddPersonalData

    /**
     * Datos personales (pueden ser NULL).
     */
    val personalData = MutableLiveData<PersonalData?>()

    /**
     * Error de RED
     */
    private val _networkError = MutableLiveData<ResultWrapper.NetworkError>()
    val networkError: LiveData<ResultWrapper.NetworkError> = _networkError

    /**
     * Error GENÉRICO enviado desde el Servidor al notificar un positivo
     */
    private val _notifyError = MutableLiveData<ResultWrapper.GenericError>()
    val notifyError: LiveData<ResultWrapper.GenericError> = _notifyError


    /**
     * LiveData para el icono de Carga.
     */
    private val _isLoading = MutableLiveData(false)
    val isLoading : LiveData<Boolean> = _isLoading


    /**
     * Notifica un nuevo positivo en el sistema. Esto implica subir todas las
     * localizaciones del usuario registradas en el dispositivo en los últimos
     * días.
     */
    fun notifyPositive() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            var infectivityPeriod = Constants.DEFAULT_INFECTIVITY_PERIOD
            // Recuperar el periodo de infectividad de la configuración del Backend.
            when(val trackerConfig = configRepository.getTrackerConfig()) {
                is ResultWrapper.Success -> {
                    infectivityPeriod = trackerConfig.value.infectivityPeriod
                }
                is ResultWrapper.NetworkError -> {
                    Log.d("Err", "Error")
                }
            }
            // Obtener localizaciones desde los últimos X días
            val startDate = Utils.formatDate(Utils.addToDate(Date(), Calendar.DATE, -1*infectivityPeriod), "yyyy-MM-dd")
            val locations = locationRepository.getLastLocationsSince(startDate)
            // Obtener fechas a las que se corresponden las localizaciones.
            val locationDates = locationRepository.getLastLocationDatesSince(startDate)
            // Crear el objeto con las localizaciones del positivo, incluyendo los datos personales
            val personalData: PersonalData? = if(_flagAddPersonalData.value!!) getPersonalData() else null
            val positiveLocations = Positive(locations, locationDates, personalData)
            // Subir los datos al servidor
            when(val result = positiveRepository.notifyPositive(positiveLocations)) {
                is ResultWrapper.NetworkError -> { _networkError.postValue(result) }
                is ResultWrapper.GenericError -> { _notifyError.postValue(result) }
                is ResultWrapper.Success -> {
                    result.value.let { _notifyPositiveResult.postValue(it) }
                }
            }
            _isLoading.postValue(false)
        }
    }

    /**
     * Establece el valor para el flag que indica si se añaden
     * o no los datos personales del usuario.
     * @param isChecked valor true o false según el estado del Checkbox.
     */
    fun setAddPersonalData (isChecked: Boolean){
        _flagAddPersonalData.value = isChecked
    }

    /**
     * Almacena los datos personales en las SharedPreferences.
     *
     * @param personalData datos personales.
     */
    fun savePersonalData(personalData: PersonalData){
        personalDataRepository.save(personalData)
    }

    /**
     * Recupera los datos personales de las SharedPreferences.
     *
     * @return datos personales.
     */
    fun getPersonalData(): PersonalData {
        return personalDataRepository.get()
    }

}