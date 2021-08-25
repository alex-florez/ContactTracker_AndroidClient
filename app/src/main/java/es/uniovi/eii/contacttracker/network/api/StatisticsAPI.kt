package es.uniovi.eii.contacttracker.network.api

import es.uniovi.eii.contacttracker.model.Installation
import es.uniovi.eii.contacttracker.network.model.CheckResult
import es.uniovi.eii.contacttracker.network.model.StatisticsResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interfaz de la API de estadísticas que representa operaciones
 * para almacenar estadísticas en la base de datos en la nube.
 */
interface StatisticsAPI {

    @POST("/statistics/registerInstall")
    suspend fun registerNewInstall(@Body body: Installation): StatisticsResponse

    @POST("/statistics/registerCheckResult")
    suspend fun registerRiskContactResult(@Body body: CheckResult): StatisticsResponse

}