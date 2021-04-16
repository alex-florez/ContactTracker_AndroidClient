package es.uniovi.eii.contacttracker.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.network.Positive
import es.uniovi.eii.contacttracker.network.model.ResultWrapper
import es.uniovi.eii.contacttracker.repositories.PositiveRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para el Fragmento de Notificar un positivo
 * y subir las coordenadas.
 */
@HiltViewModel
class NotifyPositiveViewModel @Inject constructor(
    private val positiveRepository: PositiveRepository
) : ViewModel() {

    private val _positive = MutableLiveData<Positive>()
    val positive: LiveData<Positive> = _positive


    fun getPositive() {
        viewModelScope.launch {
            when (val response = positiveRepository.getPrueba()) {
                is ResultWrapper.NetworkError -> Log.d("APIRESULT", "NetworkError")
                is ResultWrapper.GenericError -> Log.d("APIRESULT", response.responseError.toString())
                is ResultWrapper.Success -> _positive.value = response.value!!
            }
        }
    }
}