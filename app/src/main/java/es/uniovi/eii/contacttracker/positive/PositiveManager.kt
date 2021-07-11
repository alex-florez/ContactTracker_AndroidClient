package es.uniovi.eii.contacttracker.positive

import android.util.Log
import es.uniovi.eii.contacttracker.model.PersonalData
import es.uniovi.eii.contacttracker.model.Positive
import es.uniovi.eii.contacttracker.network.model.APIResult
import es.uniovi.eii.contacttracker.repositories.ConfigRepository
import es.uniovi.eii.contacttracker.repositories.LocationRepository
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
        val positive = Positive(null,
            null,
            Date(),
            locations,
            personalData
        )
        val result = positiveRepository.notifyPositive(positive)
        if(result is APIResult.Success){
            // Establecer ID y almacenar en la base de datos local.
            positive.positiveCode = result.value.positiveCode
            positiveRepository.insertPositive(positive)
            val poss = positiveRepository.getAllLocalPositives()
            Log.d("asd", poss.size.toString())
        }
        return result
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