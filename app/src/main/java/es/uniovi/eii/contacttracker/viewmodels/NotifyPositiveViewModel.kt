package es.uniovi.eii.contacttracker.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.network.Positive
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

    /**
     * Error GENÉRICO enviado desde el Servidor
     */

    fun getPositive() {
        viewModelScope.launch {
            when (val response = positiveRepository.getPrueba()) {
                is ResultWrapper.NetworkError -> Log.d("APIRESULT", "NetworkError")
                is ResultWrapper.GenericError -> Log.d("APIRESULT", response.responseError.toString())
                is ResultWrapper.Success -> _positive.value = response.value!!
            }
        }
    }

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
                is ResultWrapper.NetworkError -> {Log.d("APIRESULT", "NetworkError")}
                is ResultWrapper.GenericError -> Log.d("APIRESULT", result.responseError.toString())
                is ResultWrapper.Success -> _notifyPositiveResult.postValue(result.value!!)
            }
        }
    }

}