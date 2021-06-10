package es.uniovi.eii.contacttracker.riskcontact

import es.uniovi.eii.contacttracker.model.Positive
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.network.model.ResultWrapper
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.repositories.PositiveRepository
import es.uniovi.eii.contacttracker.riskcontact.detector.RiskContactDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject

/**
 * Clase que contiene toda la funcionalidad para gestionar
 * las comprobaciones de contactos de riesgo, incluyendo el
 * servicio en primer plano y las alarmas para las comprobaciones periódicas.
 */
class RiskContactManager @Inject constructor(
        private val detector: RiskContactDetector, // Detector de contactos de riesgo.
        private val locationRepository: LocationRepository, // Repositorio de localización.
        private val positiveRepository: PositiveRepository // Repositorio de positivos.
) {

    /**
     * Ejecuta la comprobación de contactos de riesgo teniendo en cuenta los
     * parámetros de configuración establecidos para la comprobación.
     * Recupera las localizaciones correspondientes de los positivos
     * notificados en los últimos días y también las localizaciones del propio usuario.
     *
     * Al finalizar almacena en la base de datos local los resultados obtenidos.
     */
    suspend fun checkRiskContacts() {
        // Resultado de la comprobación
        val result = RiskContactResult()
        // Obtener el alcance en DÍAS de la comprobación de las SharedPreferences.
        val scopeDays = 3

        /* Obtener el itinerario del propio usuario desde la fecha indicada */
        val userItinerary = locationRepository.getItinerarySince(scopeDays)
        /* Obtener los positivos con los que hacer la comprobación */
        var positives = listOf<Positive>()
        when(val positivesResult = positiveRepository.getPositivesFromLastDays(scopeDays)) {
            is ResultWrapper.Success -> {
                positives = positivesResult.value
            }
            else -> { // Error en la comprobación

            }
        }
        /* Hacer la comprobación para cada positivo */
        positives.forEach { positive ->
            // Itinerario del positivo
            val positiveItinerary = positive.getItinerary()
            val contacts = detector.startChecking(userItinerary, positiveItinerary)
            if(contacts.isNotEmpty()){
                // Actualizar el resultado.
                result.riskContacts.addAll(contacts)
                result.numberOfPositives += 1 // Incrementar el número de positivos.
            }
        }

        // Almacenar el resultado en la base de datos local.
    }

}