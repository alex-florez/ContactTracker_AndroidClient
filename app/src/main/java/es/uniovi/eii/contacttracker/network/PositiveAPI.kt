package es.uniovi.eii.contacttracker.network

import retrofit2.http.GET

/**
 * Cliente de la API Rest proporcionada por el backend, para
 * consumir los servicios relacionados con el registro de positivos.
 */
interface PositiveAPI {

    @GET("/prueba")
    suspend fun getPrueba(): Positive
}