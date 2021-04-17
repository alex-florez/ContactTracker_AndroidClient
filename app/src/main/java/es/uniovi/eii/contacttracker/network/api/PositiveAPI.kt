package es.uniovi.eii.contacttracker.network.api

import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.network.Positive
import es.uniovi.eii.contacttracker.network.model.NotifyPositiveResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Cliente de la API Rest proporcionada por el backend, para
 * consumir los servicios relacionados con el registro de positivos.
 */
interface PositiveAPI {

    @Headers("Content-Type:application/json")
    @GET("/getPositivo")
    suspend fun getPrueba(): Positive

    @Headers("Content-Type: application/json")
    @POST("/notifyPositive")
    suspend fun notifyPositive(@Body locations: List<UserLocation>): NotifyPositiveResult
}