package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.network.model.NotifyPositiveResult
import es.uniovi.eii.contacttracker.network.model.ResultWrapper
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.repositories.PositiveRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para el Fragmento de Notificar un positivo
 * y subir las coordenadas.
 */
@HiltViewModel
class NotifyPositiveViewModel @Inject constructor(
    private val positiveRepository: PositiveRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    /**
     * Resultado exitoso de NOTIFICAR un POSITIVO.
     */
    private val _notifyPositiveResult = MutableLiveData<NotifyPositiveResult>()
    val notifyPositiveResult: LiveData<NotifyPositiveResult> = _notifyPositiveResult

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
     * Notifica un nuevo positivo en el sistema. Esto implica subir todas las
     * localizaciones del usuario registradas en el dispositivo en los últimos
     * días.
     */
    fun notifyPositive() {
        viewModelScope.launch(Dispatchers.IO) {
            // Obtener localizaciones
            val todayLocations = locationRepository.getTodayUserLocations()
            // Subirlas al servidor
            val result = positiveRepository.notifyPositive(todayLocations)
            when(result) {
                is ResultWrapper.NetworkError -> { _networkError.postValue(result) }
                is ResultWrapper.GenericError -> { _notifyError.postValue(result) }
                is ResultWrapper.Success -> {
                    result.value.let { _notifyPositiveResult.postValue(it) }
                }
            }
        }
    }


}