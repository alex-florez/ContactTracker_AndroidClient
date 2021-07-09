package es.uniovi.eii.contacttracker.positive

import es.uniovi.eii.contacttracker.model.PersonalData
import es.uniovi.eii.contacttracker.model.Positive
import es.uniovi.eii.contacttracker.network.model.APIResult
import es.uniovi.eii.contacttracker.repositories.ConfigRepository
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.repositories.PersonalDataRepository
import es.uniovi.eii.contacttracker.repositories.PositiveRepository
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
     * @param personalData Datos personales del positivo (opcionales)
     * @return APIResult que envuelve al resultado de la notificación.
     */
    suspend fun notifyPositive(personalData: PersonalData?): APIResult<NotifyPositiveResult> {
        val infectivityPeriod = getInfectivityPeriod() // Periodo de infectividad
        // Obtener las localizaciones de los últimos días.
        val locations = locationRepository.getLastLocationsSince(infectivityPeriod)
        // Obtener las distintas fechas de las localizaciones de los últimos días.
        val locationDates = locationRepository.getLastLocationDatesSince(infectivityPeriod)
        val positive = Positive(
            null,
            Date(),
            locations,
            locationDates,
            personalData
        )
        return positiveRepository.notifyPositive(positive)
    }

    /**
     * Obtiene la configuración de la notificación de positivos
     * y devuelve el periodo de infectividad establecido actualmente.
     */
    private suspend fun getInfectivityPeriod(): Int {
        val config = configRepository.getNotifyPositiveConfig()
        return config.infectivityPeriod
    }
}