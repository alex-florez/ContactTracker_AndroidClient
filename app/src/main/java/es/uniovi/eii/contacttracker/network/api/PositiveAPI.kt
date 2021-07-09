package es.uniovi.eii.contacttracker.network.api

import es.uniovi.eii.contacttracker.positive.NotifyPositiveResult
import es.uniovi.eii.contacttracker.model.Positive
import retrofit2.http.*

/**
 * Cliente de la API Rest proporcionada por el backend, para
 * consumir los servicios relacionados con el registro de positivos.
 */
interface PositiveAPI {

    @Headers("Content-Type: application/json")
    @POST("/notifyPositive")
    suspend fun notifyPositive(@Body positive: Positive): NotifyPositiveResult

    @GET("/getPositives/{lastDays}")
    suspend fun getPositives(@Path("lastDays") lastDays: Int): List<Positive>
}