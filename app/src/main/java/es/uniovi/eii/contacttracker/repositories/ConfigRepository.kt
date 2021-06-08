package es.uniovi.eii.contacttracker.repositories

import es.uniovi.eii.contacttracker.model.TrackerConfig
import es.uniovi.eii.contacttracker.network.api.ConfigAPI
import es.uniovi.eii.contacttracker.network.apiCall
import es.uniovi.eii.contacttracker.network.model.ResultWrapper
import kotlinx.coroutines.Dispatchers
import okhttp3.Dispatcher
import javax.inject.Inject

/**
 * Repositorio de Configuración de la Aplicación.
 *
 * Contiene las operaciones relativas a la configuración
 * del sistema, opciones de rastreo y comprobación de contactos
 * de riesgo, que se obtienen desde el Backend.
 */
class ConfigRepository @Inject constructor(
        private val configAPI: ConfigAPI
) {

    /**
     * Realiza una petición al backend a través de Retrofit
     * para recuperar la configuración relativa al RASTREO.
     *
     * @return Result Wrapper con la configuración del rastreo.
     */
    suspend fun getTrackerConfig(): ResultWrapper<TrackerConfig> {
        return apiCall(Dispatchers.IO) {
            configAPI.getTrackerConfig()
        }
    }
}