package es.uniovi.eii.contacttracker.repositories

import es.uniovi.eii.contacttracker.network.api.PositiveAPI
import es.uniovi.eii.contacttracker.network.model.ResultWrapper
import es.uniovi.eii.contacttracker.network.apiCall
import es.uniovi.eii.contacttracker.model.NotifyPositiveResult
import es.uniovi.eii.contacttracker.model.Positive
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
    suspend fun notifyPositive(positive: Positive): ResultWrapper<NotifyPositiveResult> {
        return apiCall(Dispatchers.IO) {
            positiveAPI.notifyPositive(positive)
        }
    }

    /**
     * Utiliza la API Rest del backend para recuperar los datos de los positivos que tengan
     * localizaciones registradas en los últimos días, indicando dicho número de días como
     * parámetro.
     *
     * @param lastDays Número de días atrás a tener en cuenta.
     * @return ResultWrapper con la lista de positivos.
     */
    suspend fun getPositivesFromLastDays(lastDays: Int): ResultWrapper<List<Positive>> {
        return apiCall(Dispatchers.IO) {
            positiveAPI.getPositives(lastDays)
        }
    }

}