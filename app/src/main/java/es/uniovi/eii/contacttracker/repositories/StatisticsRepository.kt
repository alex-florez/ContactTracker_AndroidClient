package es.uniovi.eii.contacttracker.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.di.IoDispatcher
import es.uniovi.eii.contacttracker.model.Installation
import es.uniovi.eii.contacttracker.network.api.StatisticsAPI
import es.uniovi.eii.contacttracker.network.apiCall
import es.uniovi.eii.contacttracker.network.model.APIResult
import es.uniovi.eii.contacttracker.network.model.CheckResult
import es.uniovi.eii.contacttracker.network.model.StatisticsResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Repositorio para las estadísticas de la aplicación.
 *
 * Se encarga de recopilar datos de la aplicación y subirlos a la nube
 * almacenándolos en la base de datos.
 */
class StatisticsRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val statisticsAPI: StatisticsAPI,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    @ApplicationContext private val ctx: Context
) {

    /**
     * Registra una nueva instalación de la aplicación mediante una solicitud
     * a la API Rest de estadísticas.
     *
     * Solo se registra la instalación si es la primera vez que se inicia la
     * aplicación, comprobándolo mediante el flag almacenado en las SharedPrefs.
     * (es una forma de simular una instalación de manera virtual)
     *
     * @param timestamp Fecha y hora en la que se registró la instalación.
     * @return Respuesta de la API de estadísticas.
     */
    suspend fun registerNewInstall(timestamp: Long): APIResult<StatisticsResponse> {
        if(!sharedPreferences.contains(ctx.getString(R.string.shared_prefs_new_install)) || sharedPreferences.getBoolean(ctx.getString(R.string.shared_prefs_new_install), false)) {
            val response = apiCall(dispatcher) {
                statisticsAPI.registerNewInstall(Installation(timestamp))
            }
            // Si la respuesta es exitosa
            if(response is APIResult.Success) {
                // Actualizar flag en las SharedPreferences
                sharedPreferences.edit {
                    putBoolean(ctx.getString(R.string.shared_prefs_new_install), false)
                }
            }
            return response
        }
        return APIResult.Success(
            StatisticsResponse(
            false,
            "La instalación de la aplicación en este dispositivo ya ha sido registrada."
        )
        )
    }

    /**
     * Registra el resultado de la comprobación pasado como parámetro en la nube
     * mediante una petición a la API de estadísticas.
     *
     * @param result Resultado de la comprobación, con datos parciales a registrar.
     * @return Respuesta de la API de estadísticas.
     */
    suspend fun registerRiskContactResult(result: CheckResult): APIResult<StatisticsResponse> {
        return apiCall(dispatcher) {
            statisticsAPI.registerRiskContactResult(result)
        }
    }
}