package es.uniovi.eii.contacttracker.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uniovi.eii.contacttracker.repositories.PositiveRepository
import javax.inject.Inject

/**
 * ViewModel para el Fragmento de Notificar un positivo
 * y subir las coordenadas.
 */
@HiltViewModel
class NotifyPositiveViewModel @Inject constructor(
    private val positiveRepository: PositiveRepository
) : ViewModel() {

}