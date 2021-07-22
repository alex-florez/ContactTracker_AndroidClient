package es.uniovi.eii.contacttracker.network.model

/**
 * Clase sellada que representa un resultado de
 * una petición a la API del Backend.
 */
sealed class APIResult<out T> {

    /* Éxito con el valor esperado */
    data class Success<out T>(val value: T): APIResult<T>()

    /* Error GENÉRICO HTTP */
    data class HttpError(val code: Int? = null,
                         val responseError: ResponseError?): APIResult<Nothing>()

    /* Error de RED */
    object NetworkError: APIResult<Nothing>()
}
