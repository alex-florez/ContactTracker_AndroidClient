package es.uniovi.eii.contacttracker.network.api

import es.uniovi.eii.contacttracker.model.Installation
import es.uniovi.eii.contacttracker.network.model.CheckResult
import es.uniovi.eii.contacttracker.network.model.StatisticsResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interfaz Cliente de la API REST de estadísticas que representa operaciones
 * para almacenar estadísticas en la base de datos en la nube.
 */
interface StatisticsAPI {

    /**
     * Registra una nueva instalación de la aplicación.
     *
     * @param body Objeto que representa la instalación.
     * @return Respuesta del registro de la estadística.
     */
    @POST("/statistics/registerInstall")
    suspend fun registerNewInstall(@Body body: Installation): StatisticsResponse

    /**
     * Registra un nuevo resumen del resultado de una comproabción.
     *
     * @param body Objeto con el resumen del resultado.
     * @return Respuesta del registro de la estadística.
     */
    @POST("/statistics/registerCheckResult")
    suspend fun registerRiskContactResult(@Body body: CheckResult): StatisticsResponse

}