package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.model.*
import es.uniovi.eii.contacttracker.network.model.APIResult
import es.uniovi.eii.contacttracker.positive.NotifyPositiveResult
import es.uniovi.eii.contacttracker.positive.PositiveManager
import es.uniovi.eii.contacttracker.repositories.ConfigRepository
import es.uniovi.eii.contacttracker.repositories.PersonalDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para el Fragmento de Notificar un positivo
 * y subir las coordenadas.
 */
@HiltViewModel
class NotifyPositiveViewModel @Inject constructor(
    private val positiveManager: PositiveManager,
    private val personalDataRepository: PersonalDataRepository,
    private val configRepository: ConfigRepository
) : ViewModel() {

    /**
     * Resultado exitoso de NOTIFICAR un POSITIVO.
     */
    private val _notifyPositiveResult = MutableLiveData<NotifyPositiveResult>()
    val notifyPositiveResult: LiveData<NotifyPositiveResult> = _notifyPositiveResult

    /**
     * LiveData para el periodo de infectividad.
     */
    private val _infectivityPeriod = MutableLiveData<Int>()
    val infectivityPeriod: LiveData<Int> = _infectivityPeriod

    /**
     * Error de RED
     */
    private val _networkError = MutableLiveData<APIResult.NetworkError>()
    val networkError: LiveData<APIResult.NetworkError> = _networkError

    /**
     * Error GENÉRICO enviado desde el Servidor al notificar un positivo
     */
    private val _notifyError = MutableLiveData<APIResult.HttpError>()
    val notifyError: LiveData<APIResult.HttpError> = _notifyError


    /**
     * LiveData para el icono de Carga.
     */
    private val _isLoading = MutableLiveData(false)
    val isLoading : LiveData<Boolean> = _isLoading

    /* Icono de carga del periodo de infectividad */
    private val _loadingInfectivity = MutableLiveData(false)
    val loadingInfectivity: LiveData<Boolean> = _loadingInfectivity


    /**
     * Notifica un nuevo positivo en el sistema. Esto implica subir todas las
     * localizaciones del usuario registradas en el dispositivo en los últimos
     * días, asociando opcionalmente los datos personales del usuario.
     *
     * @param addPersonalData Flag que indica si se deben añadir o no los datos personales.
     */
    fun notifyPositive(addPersonalData: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            // Datos personales
            val personalData = if(addPersonalData) getPersonalData() else null
            when(val result = positiveManager.notifyPositive(personalData)){
                is APIResult.NetworkError -> { _networkError.postValue(result) }
                is APIResult.HttpError -> { _notifyError.postValue(result) }
                is APIResult.Success -> { _notifyPositiveResult.postValue(result.value) }
            }
            _isLoading.postValue(false)
        }
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

    /**
     * Carga desde la configuración el número de días
     * correspondientes al periodo de infectividad.
     */
    fun loadInfectivityPeriod() {
        viewModelScope.launch(Dispatchers.IO) {
            _loadingInfectivity.postValue(true)
            _infectivityPeriod.postValue(configRepository.getNotifyPositiveConfig().infectivityPeriod)
            _loadingInfectivity.postValue(false)
        }
    }
}