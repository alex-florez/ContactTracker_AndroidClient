package es.uniovi.eii.contacttracker.repositories

import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.network.api.PositiveAPI
import es.uniovi.eii.contacttracker.network.model.ResultWrapper
import es.uniovi.eii.contacttracker.network.apiCall
import es.uniovi.eii.contacttracker.network.model.NotifyPositiveResult
import es.uniovi.eii.contacttracker.network.model.PositiveLocations
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


    /**
     * Realiza una llamada a la API Rest del servidor para notificar un positivo.
     * Recibe como parámetro el objeto que contiene las localizaciones del positivo,
     * junto con las fechas correspondientes. Devuelve el resultado enviado desde el servidor.
     *
     * @param positive datos del positivo.
     * @return result wrapper con los datos del servidor.
     */
    suspend fun notifyPositive(positive: PositiveLocations): ResultWrapper<NotifyPositiveResult> {
        return apiCall(Dispatchers.IO) {
            positiveAPI.notifyPositive(positive)
        }
    }

}