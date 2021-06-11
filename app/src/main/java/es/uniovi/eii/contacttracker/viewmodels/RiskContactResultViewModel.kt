package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.model.ResultWithRiskContacts
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.repositories.RiskContactRepository
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

    fun getRiskContactResults() {
        viewModelScope.launch {
            _results.value = riskContactRepository.getAll()
        }
    }

}