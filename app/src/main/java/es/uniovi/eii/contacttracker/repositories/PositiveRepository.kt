package es.uniovi.eii.contacttracker.repositories

import es.uniovi.eii.contacttracker.di.IoDispatcher
import es.uniovi.eii.contacttracker.network.api.PositiveAPI
import es.uniovi.eii.contacttracker.network.model.APIResult
import es.uniovi.eii.contacttracker.network.apiCall
import es.uniovi.eii.contacttracker.network.model.NotifyPositiveResponse
import es.uniovi.eii.contacttracker.model.Positive
import es.uniovi.eii.contacttracker.room.mappers.toPositive
import es.uniovi.eii.contacttracker.room.daos.PositiveDao
import es.uniovi.eii.contacttracker.room.relations.PositiveUserLocationCrossRef
import es.uniovi.eii.contacttracker.util.DateUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import java.util.Date

/**
 * Repositorio de Positivos en COVID-19.
 *
 * Se encarga de notificar y registrar positivos a partir
 * del cliente de la API Rest del Backend.
 */
class PositiveRepository @Inject constructor(
    private val positiveAPI: PositiveAPI,
    private val positiveDao: PositiveDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {


    /**
     * Realiza una llamada a la API Rest del servidor para notificar un positivo.
     * Recibe como parámetro el objeto que contiene las localizaciones del positivo,
     * junto con las fechas correspondientes. Devuelve el resultado enviado desde el servidor.
     *
     * @param positive datos del positivo.
     * @return result wrapper con los datos del servidor.
     */
    suspend fun notifyPositive(positive: Positive): APIResult<NotifyPositiveResponse> {
        return apiCall(dispatcher) {
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
    suspend fun getPositivesFromLastDays(lastDays: Int): APIResult<List<Positive>> {
        return apiCall(dispatcher) {
            positiveAPI.getPositives(Date().time, lastDays)
        }
    }

    /**
     * Inserta el positivo pasado como parámetro en la base
     * de datos local.
     *
     * @param positive Objeto Positive con las localizaciones.
     */
    suspend fun insertPositive(positive: Positive) {
        val positiveID = positiveDao.insert(positive) // Insertar el objeto Positive
        positive.locations.forEach {
            if(it.userlocationID != null){
                val crossRef = PositiveUserLocationCrossRef(positiveID, it.userlocationID!!) // Insertar la referencia cruzada
                positiveDao.insert(crossRef)
            }
        }
    }

    /**
     * Devuelve todos los positivos almacenados en local.
     */
    suspend fun getAllLocalPositives(): List<Positive> {
        val wrapper = positiveDao.getAllPositives()
        return wrapper.map {
            toPositive(it)
        }
    }

    /**
     * Devuelve todos los códigos de los positivos notificados
     * por el propio usuario, es decir, los positivos almacenados
     * en el dispositivo.
     */
    suspend fun getAllLocalPositiveCodes(): List<String> {
        return positiveDao.getAllPositiveCodes()
    }

    /**
     * Consulta el número de positivos que fueron notificados en
     * la fecha pasada como parámetro.
     *
     * @param date Fecha por la que filtrar.
     * @return Número de positivos notificados en la fecha indicada.
     */
    suspend fun getNumberOfLocalPositivesNotifiedAt(date: Date): Int {
        val formattedDate = DateUtils.formatDate(date, "yyyy-MM-dd")
        return positiveDao.getNumberOfNotifiedPositivesAt(formattedDate)
    }

    /**
     * Devuelve el último positivo notificado por el usuario en este dispositivo.
     */
    suspend fun getLastNotifiedPositive(): Positive? {
        return positiveDao.getLastNotifiedPositive()
    }

    /**
     * Actualiza el positivo con los nuevos datos.
     */
    suspend fun updatePositive(positive: Positive) {
        positiveDao.update(positive)
    }
}