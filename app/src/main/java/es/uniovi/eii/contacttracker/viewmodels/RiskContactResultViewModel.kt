package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.model.ResultWithRiskContacts
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.repositories.RiskContactRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RiskContactResultViewModel @Inject constructor (
    private val riskContactRepository: RiskContactRepository
) : ViewModel(){

    /**
     * LiveData con la lista de Resultados de
     * las comprobaciones.
     */
    private val _results = MutableLiveData<List<RiskContactResult>>(listOf())
    val results: LiveData<List<RiskContactResult>> = _results

    /**
     * LiveData para el icono de progreso de carga de los
     * resultados de la comprobación.
     */
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * LiveData que indica si la lista de resultados
     * está vacía.
     */
    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty : LiveData<Boolean> = _isEmpty

    /**
     * Rellena el LiveData con todos los resultados de las
     * comprobaciones realizadas.
     */
    fun getRiskContactResults() {
        viewModelScope.launch {
            _isLoading.value = true
            _results.value = riskContactRepository.getAll()
            _isLoading.value = false
            _isEmpty.value = _results.value?.isEmpty()
        }
    }

}