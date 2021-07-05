package es.uniovi.eii.contacttracker.repositories

import android.content.SharedPreferences
import es.uniovi.eii.contacttracker.model.*
import es.uniovi.eii.contacttracker.network.api.ConfigAPI
import es.uniovi.eii.contacttracker.network.apiCall
import es.uniovi.eii.contacttracker.network.model.APIResult
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Repositorio de Configuración de la Aplicación.
 *
 * Contiene las operaciones relativas a la configuración
 * del sistema, opciones de rastreo, comprobación de contactos
 * de riesgo y notificación de positivos. La configuración se
 * recupera del Backend y también del dispositivo local.
 */
class ConfigRepository @Inject constructor(
        private val configAPI: ConfigAPI,
        private val sharedPrefs: SharedPreferences
) {

    /**
     * Realiza una petición al backend a través de Retrofit
     * para recuperar la configuración relativa al RASTREO
     * de ubicación.
     *
     * @return Objeto con las opciones de configuración del rastreo.
     */
    fun getTrackerConfig(): TrackerConfig {
        return TrackerConfig(
            sharedPrefs.getLong(TRACKER_MIN_INTERVAL_KEY, DEFAULT_MIN_INTERVAL),
            sharedPrefs.getFloat(TRACKER_SMALLEST_DISPLACEMENT_KEY, DEFAULT_SMALLEST_DISPLACEMENT)
        )
    }

    /**
     * Hace una petición a la API Rest y también recupera
     * las opciones de configuración almacenadas en local
     * para devolver un objeto de configuración de la comprobación
     * de contactos de riesgo.
     *
     * @return Objeto de configuración de la comprobación de contactos de riesgo.
     */
    suspend fun getRiskContactConfig(): RiskContactConfig {
        var config = RiskContactConfig()
        /* Recuperar configuración remota */
        when(val result = apiCall(Dispatchers.IO){ configAPI.getRiskContactConfig() }){
            is APIResult.Success -> {
                config = result.value
            }
        }
        /* Recuperar configuración de las SharedPrefs */
        config.checkScope = sharedPrefs.getInt(RISK_CONTACT_CHECK_SCOPE_KEY, SCOPE)
        return config
    }

    /**
     * Realiza una llamada al Backend para recuperar la configuración
     * de la notificación de positivos.
     *
     * @return Objeto con la configuración de la notificación de positivos.
     */
    suspend fun getNotifyPositiveConfig(): NotifyPositiveConfig {
        var notifyPositiveConfig = NotifyPositiveConfig()
        when(val result = apiCall(Dispatchers.IO) {configAPI.getNotifyPositiveConfig()}){
            is APIResult.Success -> {
                notifyPositiveConfig = result.value
            }
        }
        return notifyPositiveConfig
    }

}