package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.di.IoDispatcher
import es.uniovi.eii.contacttracker.model.*
import es.uniovi.eii.contacttracker.positive.PositiveManager
import es.uniovi.eii.contacttracker.repositories.ConfigRepository
import es.uniovi.eii.contacttracker.repositories.PersonalDataRepository
import es.uniovi.eii.contacttracker.util.SingleLiveEvent
import es.uniovi.eii.contacttracker.util.ValueWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.Date

/**
 * ViewModel para el Fragmento de Notificar un positivo y
 * subir las coordenadas registradas en la base de datos local.
 */
@HiltViewModel
class NotifyPositiveViewModel @Inject constructor(
    private val positiveManager: PositiveManager,
    private val personalDataRepository: PersonalDataRepository,
    private val configRepository: ConfigRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
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
    private val _notifySuccess = SingleLiveEvent<Pair<Int, Int>>()
    val notifySuccess: LiveData<Pair<Int, Int>> = _notifySuccess

    /**
     * LiveData con el código del String que representa un error determinado
     * en la notificación del positivo. Es un Par de tipo Int por si en algún
     * error es necsario mostrar algún valor.
     */
    private val _notifyError = SingleLiveEvent<Pair<Int, Int?>>()
    val notifyError: LiveData<Pair<Int, Int?>> = _notifyError

    /**
     * LiveData para el icono de Carga.
     */
    private val _isLoading = MutableLiveData(false)
    val isLoading : LiveData<Boolean> = _isLoading

    /* Icono de carga del periodo de infectividad */
    private val _loadingInfectivity = MutableLiveData(false)
    val loadingInfectivity: LiveData<Boolean> = _loadingInfectivity

    /* Listado de positivos almacenados en local */
    private val _localPositives = MutableLiveData<List<Positive>>()
    val localPositives: LiveData<List<Positive>> = _localPositives


    /**
     * Notifica un nuevo positivo en el sistema. Esto implica subir todas las
     * localizaciones del usuario registradas en el dispositivo en los últimos
     * días, asociando opcionalmente los datos personales del usuario. También se
     * incluyen las respuestas a las preguntas realizadas al usuario.
     *
     * @param addPersonalData Flag que indica si se deben añadir o no los datos personales.
     * @param answers Respuestas a las preguntas del último diálogo para notificar un positivo.
     * @param date Fecha actual en la que se notifica el positivo.
     */
    fun notifyPositive(addPersonalData: Boolean, answers: Map<String, Boolean>, date: Date) {
        viewModelScope.launch(dispatcher) {
            _isLoading.postValue(true)
            // Datos personales
            val personalData = if(addPersonalData) getPersonalData() else null
            when(val result = positiveManager.notifyPositive(personalData, answers, date)) {
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
     * Devuelve una lista de positivos almacenados en local. Utilizado para
     * el Testing de los viewmodels.
     */
    fun getLocalPositives() {
        viewModelScope.launch(dispatcher) {
            _localPositives.postValue(positiveManager.getLocalPositives())
        }
    }

    /**
     * Carga desde la configuración el número de días
     * correspondientes al periodo de infectividad.
     */
    fun loadInfectivityPeriod() {
        viewModelScope.launch(dispatcher) {
            _loadingInfectivity.postValue(true)
            _infectivityPeriod.postValue(configRepository.getNotifyPositiveConfig().infectivityPeriod)
            _loadingInfectivity.postValue(false)
        }
    }

    /**
     * Procesa el error de notificación de positivo devolviendo el código
     * del string correspondiente al error y un valor Int si es aplicable.
     */
    private suspend fun processError(error: Error): Pair<Int, Int?> {
        // Recuperar la configuración de notificación
        val config = configRepository.getNotifyPositiveConfig()
        return when(error) {
            Error.TIMEOUT -> Pair(R.string.network_error, null)
            Error.CANNOT_NOTIFY -> Pair(R.string.genericErrorNotifyPositive, null)
            Error.NOTIFICATION_LIMIT_EXCEEDED -> Pair(R.string.errorNotifyLimitExceeded, config.notifyWaitTime)
            Error.NO_LOCATIONS_TO_NOTIFY -> Pair(R.string.errorNotifyNoLocations, null)
            else -> Pair(R.string.genericError, null)
        }
    }
}