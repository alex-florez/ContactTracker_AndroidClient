package es.uniovi.eii.contacttracker.network.api

import es.uniovi.eii.contacttracker.model.NotifyPositiveConfig
import es.uniovi.eii.contacttracker.model.RiskContactConfig
import es.uniovi.eii.contacttracker.model.TrackerConfig
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * Cliente de la API Rest expuesta por el Backend para
 * obtener información sobre la configuración del sistema
 * y de las opciones del rastreo y comprobación de contactos.
 */
interface ConfigAPI {

    @Headers("Content-Type: application/json")
    @GET("/config/notify-config")
    suspend fun getNotifyPositiveConfig(): NotifyPositiveConfig

    @Headers("Content-Type: application/json")
    @GET("/config/risk-contact-config")
    suspend fun getRiskContactConfig(): RiskContactConfig
}