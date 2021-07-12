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
        // Configuración de la notificación de positivos.
        val config = configRepository.getNotifyPositiveConfig()
        // Comprobar límite de notificación de positivos.
        if(checkNotifyLimit(config.notifyLimit)){
            // Obtener las localizaciones de los últimos días.
            val locations = locationRepository.getLastLocationsSince(config.infectivityPeriod)
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
        } else {
            return APIResult.Success(NotifyPositiveResult(
                null, 0, true
            ))
        }
    }

    /**
     * Comprueba que no se haya superado el límite de
     * notificación de positivos.
     */
    private suspend fun checkNotifyLimit(limit: Int): Boolean {
        // N.º de positivos notificados en la fecha de hoy.
        val notifiedPositivesToday = positiveRepository.getNumberOfLocalPositivesNotifiedAt(Date())
        return notifiedPositivesToday < limit
    }
}