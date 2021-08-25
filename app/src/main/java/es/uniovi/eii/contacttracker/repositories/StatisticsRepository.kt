package es.uniovi.eii.contacttracker.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.network.api.StatisticsAPI
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
    @ApplicationContext private val ctx: Context
) {

    /**
     * Registra una nueva instalación de la aplicación mediante una solicitud
     * a la API Rest de estadísticas.
     *
     * Solo se registra la instalación si es la primera vez que se inicia la
     * aplicación, comprobándolo mediante el flag almacenado en las SharedPrefs.
     * (es una forma de simular una instalación de manera virtual)
     */
    suspend fun registerNewInstall() {
        if(!sharedPreferences.contains(ctx.getString(R.string.shared_prefs_new_install))
            || sharedPreferences.getBoolean(ctx.getString(R.string.shared_prefs_new_install), false)) {
            // Primera vez abierta la app, registrar una instalación.
            statisticsAPI.registerNewInstall()
            // Actualizar flag
            sharedPreferences.edit {
                putBoolean(ctx.getString(R.string.shared_prefs_new_install), false)
            }
        }
    }
}