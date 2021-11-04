package es.uniovi.eii.contacttracker.network.api

import es.uniovi.eii.contacttracker.model.NotifyPositiveConfig
import es.uniovi.eii.contacttracker.model.RiskContactConfig
import es.uniovi.eii.contacttracker.model.TrackerConfig
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * Cliente de la API REST expuesta por el Backend para
 * obtener información sobre la configuración del sistema:
 *      - Notificación de positivos
 *      - Comprobación de contactos.
 */
interface ConfigAPI {

    /**
     * Recupera la configuración de la notificación de positivos.
     *
     * @return Objeto con la configuración de la notificación.
     */
    @Headers("Content-Type: application/json")
    @GET("/config/notify-config")
    suspend fun getNotifyPositiveConfig(): NotifyPositiveConfig

    /**
     * Recupera la configuración de la comprobación de contactos.
     *
     * @return Objeto con la configuración de la comprobación.
     */
    @Headers("Content-Type: application/json")
    @GET("/config/risk-contact-config")
    suspend fun getRiskContactConfig(): RiskContactConfig
}