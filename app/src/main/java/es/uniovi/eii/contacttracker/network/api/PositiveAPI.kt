package es.uniovi.eii.contacttracker.network.api

import es.uniovi.eii.contacttracker.network.Positive
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * Cliente de la API Rest proporcionada por el backend, para
 * consumir los servicios relacionados con el registro de positivos.
 */
interface PositiveAPI {

    @Headers("Content-Type:application/json")
    @GET("/getPositivo")
    suspend fun getPrueba(): Positive
}