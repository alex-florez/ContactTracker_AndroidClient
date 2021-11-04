package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.repositories.RiskContactRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para el fragmento del listado de resultados de
 * las comprobaciones de contactos ejecutadas.
 */
@HiltViewModel
class RiskContactResultViewModel @Inject constructor (
    riskContactRepository: RiskContactRepository
) : ViewModel(){

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
    private val _isEmpty = MediatorLiveData<Boolean>()
    val isEmpty : MediatorLiveData<Boolean> = _isEmpty

    /* LiveData para el listado de resultados de comprobación */
    val results : LiveData<List<RiskContactResult>> = riskContactRepository.getAll()

    // Constructor
    init {
        isEmpty.addSource(results) {
            _isEmpty.value = it.isEmpty()
        }
    }
}