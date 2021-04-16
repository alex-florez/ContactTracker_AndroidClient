package es.uniovi.eii.contacttracker.network

/**
 * Sealed Class para envolver al resultado
 * obtenido de la respuesta del Backend.
 */
sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T): ResultWrapper<T>()
    data class GenericError(val code: Int? = null): ResultWrapper<Nothing>()
    object NetworkError: ResultWrapper<Nothing>()
}
