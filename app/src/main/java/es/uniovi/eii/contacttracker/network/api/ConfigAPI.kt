package es.uniovi.eii.contacttracker.network.api

import es.uniovi.eii.contacttracker.model.TrackerConfig
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * Cliente de la API Rest expuesta por el Backend para
 * obtener información sobre la configuración del sistema
 * y de las opciones del rastreo.
 */
interface ConfigAPI {

    @Headers("Content-Type: application/json")
    @GET("/config/tracker-config")
    suspend fun getTrackerConfig(): TrackerConfig
}