package es.uniovi.eii.contacttracker.positive

import es.uniovi.eii.contacttracker.fragments.dialogs.notifyquestions.ASYMPTOMATIC_QUESTION
import es.uniovi.eii.contacttracker.fragments.dialogs.notifyquestions.VACCINATED_QUESTION
import es.uniovi.eii.contacttracker.model.Error
import es.uniovi.eii.contacttracker.model.PersonalData
import es.uniovi.eii.contacttracker.model.Positive
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.network.model.APIResult
import es.uniovi.eii.contacttracker.network.model.NotifyPositiveResponse
import es.uniovi.eii.contacttracker.repositories.ConfigRepository
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.repositories.PositiveRepository
import es.uniovi.eii.contacttracker.util.DateUtils
import es.uniovi.eii.contacttracker.util.ValueWrapper
import javax.inject.Inject
import java.util.Date

/**
 * Manager que se encarga de la notificación de positivos.
 */
class PositiveManager @Inject constructor(
    private val positiveRepository: PositiveRepository,
    private val locationRepository: LocationRepository,
    private val configRepository: ConfigRepository
) {

    /**
     * Recupera las últimas localizaciones del usuario que ha dado positivo en
     * función del periodo de infectividad obtenido de la configuración y hace
     * una petición al backend para almacenarlas.
     *
     * @param personalData Datos personales del positivo (opcionales)
     * @param answers Respuestas a las preguntas solicitadas al notificar un positivo.
     * @param date Fecha en la que se realiza la notificación.
     * @return ValueWrapper que envuelve el resultado de la notificación.
     */
    suspend fun notifyPositive(personalData: PersonalData?,
                                answers: Map<String, Boolean>,
                                date: Date): ValueWrapper<NotifyPositiveResponse> {
        // Configuración de la notificación de positivos.
        val config = configRepository.getNotifyPositiveConfig()
        // Comprobar límite de notificación de positivos.
        if(checkNotifyLimit(config.notifyWaitTime, date)){
            // Obtener las localizaciones de los últimos días.
            val locations = locationRepository.getLastLocationsSince(config.infectivityPeriod, date)
            return if(checkLocations(locations)){ // Comprobar que existan localizaciones
                val positive = Positive(null,
                    null,
                    date,
                    locations,
                    personalData,
                    answers[ASYMPTOMATIC_QUESTION] ?: false,
                    answers[VACCINATED_QUESTION] ?: false
                )
                processNotifyResult(positiveRepository.notifyPositive(positive), positive)
            } else {
                // No hay localizaciones para notificar
                ValueWrapper.Fail(Error.NO_LOCATIONS_TO_NOTIFY)
            }
        } else {
            // Se ha excedido el límite de notificación
            return ValueWrapper.Fail(Error.NOTIFICATION_LIMIT_EXCEEDED)
        }
    }

    /**
     * Devuelve un listado con todos los positivos notificados almacenados
     * en el dispositivo local.
     */
    suspend fun getLocalPositives(): List<Positive> {
        return positiveRepository.getAllLocalPositives()
    }


    /**
     * Comprueba que hayan transcurrido al menos el número de días
     * indicados como parámetro para poder notificar otro positivo.
     *
     * @param days Número de días que deben transcurrir.
     * @param date Fecha con la que comparar.
     */
    private suspend fun checkNotifyLimit(days: Int, date: Date): Boolean {
        // Obtener el último positivo notificado
        val lastPositive = positiveRepository.getLastNotifiedPositive()
        lastPositive?.let { positive ->
            // Obtener días transcurridos entre la fecha actual y la del último positivo
            val elapsedDays = DateUtils.getDaysBetweenDates(positive.timestamp, date)
            return elapsedDays >= days
        }
        return true
    }

    /**
     * Comprueba que la lista de localizaciones indicada no esté vacía.
     */
    private fun checkLocations(locations: List<UserLocation>): Boolean {
        return locations.isNotEmpty()
    }

    /**
     * Procesa el resultado de notificar un positivo mediante la API
     * de positivos. Si hay éxito, actualiza el positivo pasado como parámetro
     * con el código de positivo recibido mediante la API, y lo almacena en la base
     * de datos, devolviendo un ValueWrapper de éxito. Por el contrario, devuelve un
     * ValueWrapper de fallo con el error correspondiente.
     *
     * @param response Resultado de la API de notificar un positivo.
     * @param positive Positivo a notificar.
     * @return ValueWrapper que envuelve un objeto de éxito o de fallo.
     */
    private suspend fun processNotifyResult(response: APIResult<NotifyPositiveResponse>, positive: Positive): ValueWrapper<NotifyPositiveResponse> {
        return when(response) {
            is APIResult.Success -> {
                // Establecer ID y almacenar en la base de datos local.
                positive.positiveCode = response.value.positiveCode
                positiveRepository.insertPositive(positive)
                ValueWrapper.Success(response.value)
            }
            is APIResult.NetworkError -> {
                ValueWrapper.Fail(Error.TIMEOUT)
            }
            is APIResult.HttpError -> {
                ValueWrapper.Fail(Error.CANNOT_NOTIFY)
            }
        }
    }
}