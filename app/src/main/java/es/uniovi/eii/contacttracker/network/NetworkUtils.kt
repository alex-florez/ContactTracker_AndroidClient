package es.uniovi.eii.contacttracker.network

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * Método para realizar una llamada a la API Rest de manera segura.
 * Permite gestionar la respuesta en función de si ha sido exitosa o
 * ha ocurrido algún error.
 *
 * @param dispatcher Dispatcher en el que se ejecutará la corrutina.
 * @param call suspend function que representa la llamada a la API.
 */
suspend fun <T> apiCall(dispatcher: CoroutineDispatcher, call: suspend () -> T): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(call.invoke()) // Éxito
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> ResultWrapper.NetworkError // Error de RED
                is HttpException -> { // Excepción Genérica
                    val code = throwable.code()
                    ResultWrapper.GenericError(code)
                }
                else -> {
                    ResultWrapper.GenericError(null)
                }
            }
        }
    }
}