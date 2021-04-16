package es.uniovi.eii.contacttracker.repositories

import es.uniovi.eii.contacttracker.network.Positive
import es.uniovi.eii.contacttracker.network.api.PositiveAPI
import es.uniovi.eii.contacttracker.network.model.ResultWrapper
import es.uniovi.eii.contacttracker.network.apiCall
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Repositorio de Positivos en COVID-19.
 *
 * Se encarga de notificar y registrar positivos a partir
 * del cliente de la API Rest del Backend.
 */
class PositiveRepository @Inject constructor(
    private val positiveAPI: PositiveAPI
) {

    suspend fun getPrueba(): ResultWrapper<Positive> {
       return apiCall(Dispatchers.IO){
           positiveAPI.getPrueba()
       }
    }

}