package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.model.*
import es.uniovi.eii.contacttracker.network.model.APIResult
import es.uniovi.eii.contacttracker.positive.NotifyPositiveResult
import es.uniovi.eii.contacttracker.positive.PositiveManager
import es.uniovi.eii.contacttracker.repositories.ConfigRepository
import es.uniovi.eii.contacttracker.repositories.PersonalDataRepository
import es.uniovi.eii.contacttracker.util.AndroidUtils
import es.uniovi.eii.contacttracker.util.ValueWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

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
     * LiveData para el periodo de infectividad.
     */
    private val _infectivityPeriod = MutableLiveData<Int>()
    val infectivityPeriod: LiveData<Int> = _infectivityPeriod

    /**
     * Contiene un par con el Código del String de éxito de notificación
     * y el número de localizaciones que se han subido a la nube.
     */
    private val _notifySuccess = MutableLiveData<Pair<Int, Int>>()
    val notifySuccess: LiveData<Pair<Int, Int>> = _notifySuccess

    /**
     * LiveData con el código del String que representa un error determinado
     * en la notificación del positivo.
     */
    private val _notifyError = MutableLiveData<Int>()
    val notifyError: LiveData<Int> = _notifyError

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
     * días, asociando opcionalmente los datos personales del usuario. También se
     * incluyen las respuestas a las preguntas realizadas al usuario.
     *
     * @param addPersonalData Flag que indica si se deben añadir o no los datos personales.
     * @param answers Respuestas a las preguntas del último diálogo para notificar un positivo.
     */
    fun notifyPositive(addPersonalData: Boolean, answers: Map<String, Boolean>) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            // Datos personales
            val personalData = if(addPersonalData) getPersonalData() else null
            val result = positiveManager.notifyPositive(personalData, answers)
            when(result) {
                is ValueWrapper.Success -> {
                    _notifySuccess.postValue(Pair(
                        R.string.notifyPositiveResultText, // Código del String
                        result.value.uploadedLocations)) // Localizaciones subidas
                }
                is ValueWrapper.Fail -> {
                    _notifyError.postValue(processError(result.error))
                }
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

    /**
     * Procesa el error de notificación de positivo devolviendo el código
     * del string correspondiente al error.
     */
    private fun processError(error: Error): Int {
        return when(error) {
            Error.TIMEOUT -> R.string.network_error
            Error.CANNOT_NOTIFY -> R.string.genericErrorNotifyPositive
            Error.NOTIFICATION_LIMIT_EXCEEDED -> R.string.errorNotifyLimitExceeded
            Error.NO_LOCATIONS_TO_NOTIFY -> R.string.errorNotifyNoLocations
            else -> R.string.genericError
        }
    }
}