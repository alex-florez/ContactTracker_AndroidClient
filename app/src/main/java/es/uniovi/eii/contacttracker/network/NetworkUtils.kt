package es.uniovi.eii.contacttracker.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import es.uniovi.eii.contacttracker.network.model.ResponseError
import es.uniovi.eii.contacttracker.network.model.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
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
                    // Parsear datos del error al objeto de Dominio
                    val code = throwable.code()
                    val responseError = Gson().fromJson(throwable.response()?.errorBody()?.charStream(), ResponseError::class.java)
                    ResultWrapper.GenericError(code, responseError)
                }
                else -> {
                    ResultWrapper.GenericError(null, null)
                }
            }
        }
    }
}
