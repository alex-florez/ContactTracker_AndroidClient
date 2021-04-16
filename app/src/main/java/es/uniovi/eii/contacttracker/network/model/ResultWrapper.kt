package es.uniovi.eii.contacttracker.network.model

/**
 * Sealed Class para envolver al resultado
 * obtenido de la respuesta del Backend.
 */
sealed class ResultWrapper<out T> {

    // Clase de ÉXITO
    data class Success<out T>(val value: T): ResultWrapper<T>()

    // Error GENÉRICO
    data class GenericError(val code: Int? = null,
                            val responseError: ResponseError?): ResultWrapper<Nothing>()

    // Error de RED
    object NetworkError: ResultWrapper<Nothing>()
}
