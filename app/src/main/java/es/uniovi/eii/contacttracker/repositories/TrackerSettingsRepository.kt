package es.uniovi.eii.contacttracker.repositories

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.R
import javax.inject.Inject

/**
 * Repositorio de parámetros de configuración del Tracker.
 *
 * Contiene los métodos necesarios para leer y modificar los parámetros
 * de configuración para el rastreador de ubicación.
 */
class TrackerSettingsRepository @Inject constructor(
    @ApplicationContext private val ctx: Context
){

    /**
     * Referencian a las SharedPreferences.
     */
    private val sharedPrefs = ctx.getSharedPreferences(
            ctx.getString(R.string.shared_prefs_file_name), Context.MODE_PRIVATE)


    /**
     * Inserta en las sharedPreferences el valor del
     * INTERVALO MÍNIMO DE TIEMPO que tiene que pasar entre
     * actualizaciones de localización.
     *
     * @param millis intervalo de tiempo en milisegundos.
     */
    fun setMinInterval(millis: Long) {
        with(sharedPrefs.edit()) {
            putLong(ctx.getString(R.string.shared_prefs_tracker_config_min_interval), millis)
            apply()
        }
    }

    /**
     * Lee de las SharedPreferences el INTERVALO
     * MÍNIMO DE TIEMPO que tiene que pasar entre actualizaciones.
     *
     * @return intervalo de tiempo en milisegundos o -1 si no existe.
     */
    fun getMinInterval(): Long {
        return sharedPrefs.getLong(ctx.getString(R.string.shared_prefs_tracker_config_min_interval), 3000L)
    }
}