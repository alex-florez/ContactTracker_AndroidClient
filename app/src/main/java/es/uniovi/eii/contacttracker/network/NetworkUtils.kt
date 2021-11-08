package es.uniovi.eii.contacttracker.network

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import es.uniovi.eii.contacttracker.network.model.ResponseError
import es.uniovi.eii.contacttracker.network.model.APIResult
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
suspend fun <T> apiCall(dispatcher: CoroutineDispatcher, call: suspend () -> T): APIResult<T> {
    return withContext(dispatcher) {
        try {
            APIResult.Success(call.invoke()) // Éxito
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> APIResult.NetworkError // Error de RED
                is HttpException -> { // Excepción Genérica
                    // Parsear datos del error al objeto de Dominio
                    val code = throwable.code()
                    var responseError: ResponseError?
                    responseError = try {
                        Gson().fromJson(throwable.response()?.errorBody()?.charStream(), ResponseError::class.java)
                    } catch (e: JsonSyntaxException) {
                        null
                    }
                    APIResult.HttpError(code, responseError)
                }
                else -> APIResult.HttpError(null, null)
            }
        }
    }
}
