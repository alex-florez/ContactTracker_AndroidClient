package es.uniovi.eii.contacttracker.network.api

import es.uniovi.eii.contacttracker.network.model.NewInstallResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Interfaz de la API de estadísticas que representa operaciones
 * para almacenar estadísticas en la base de datos en la nube.
 */
interface StatisticsAPI {

    @POST("/statistics/registerInstall")
    suspend fun registerNewInstall(@Body body: Any = Object()): NewInstallResponse

}