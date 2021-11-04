package es.uniovi.eii.contacttracker.network.api

import es.uniovi.eii.contacttracker.network.model.NotifyPositiveResponse
import es.uniovi.eii.contacttracker.model.Positive
import retrofit2.http.*

/**
 * Cliente de la API REST proporcionada por el backend, para
 * consumir los servicios relacionados con el registro de positivos.
 */
interface PositiveAPI {

    /**
     * Notifica un nuevo positivo en el sistema.
     *
     * @param positive Objeto con los datos del positivo y su itinerario.
     * @return Respuesta de la notificación del positivo.
     */
    @Headers("Content-Type: application/json")
    @POST("/positive/notifyPositive")
    suspend fun notifyPositive(@Body positive: Positive): NotifyPositiveResponse

    /**
     * Devuelve una lista con los positivos que tengan localizaciones registradas
     * en el rango definido por [targetDate, targetDate - lastDays], es decir,
     * en alguno de los últimos días contemplados desde la fecha de referencia y el
     * número de días atrás.
     */
    @GET("/positive/getPositives/{targetDateMillis}/{lastDays}")
    suspend fun getPositives(@Path("targetDateMillis") targetDateMillis: Long,
                             @Path("lastDays") lastDays: Int): List<Positive>
}