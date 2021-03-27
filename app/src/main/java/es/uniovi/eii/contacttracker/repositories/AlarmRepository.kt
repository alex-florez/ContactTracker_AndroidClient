package es.uniovi.eii.contacttracker.repositories

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.R
import java.util.Date
import javax.inject.Inject

/**
 * Repositorio de Alarmas de Localización.
 *
 * Contiene las operaciones relacionadas con las alarmas de localización y el
 * rastreo automático de ubicación.
 */
class AlarmRepository @Inject constructor(
    @ApplicationContext private val ctx: Context
) {

    /**
     * Referencia a las shared Prefs
     */
    private val sharedPrefs = ctx.getSharedPreferences(
        ctx.getString(R.string.shared_prefs_file_name), Context.MODE_PRIVATE)


    /**
     * Establece la fecha pasada como parámetro en las shared
     * preferences según sea de tipo inicio o de fin.
     *
     * @param date Fecha de inicio de la alarma.
     * @param type Inicio o Fin.
     */
    fun setLocationAlarmTime(date: Date, type: Int) {
        with(sharedPrefs.edit()) {
            putLong(ctx.getString(type), date.time)
            apply()
        }
    }

    /**
     * Recibe como parámetro la clave del tipo de fecha y devuelve
     * la fecha de la alarma asociada a la clave.
     *
     * @param type tipo de fecha (inicio o fin).
     * @return fecha asociada.
     */
    fun getLocationAlarmTime(type: Int): Date {
        val longDate = sharedPrefs.getLong(
            ctx.getString(type), Date().time)
        return Date(longDate)
    }

    /**
     * Comprueba si existe una entrada en las SharedPreferences
     * con la clave key pasada como parámetro.
     *
     * @param key clave a comprobar.
     * @return boolean
     */
    fun checkKey(key: Int): Boolean {
        return sharedPrefs.contains(ctx.getString(key))
    }

    /**
     * Elimina la Shared Preference de clave pasada como parámetro.
     */
    fun remove(key: Int) {
        with(sharedPrefs.edit()){
            remove(ctx.getString(key))
            apply()
        }
    }

}