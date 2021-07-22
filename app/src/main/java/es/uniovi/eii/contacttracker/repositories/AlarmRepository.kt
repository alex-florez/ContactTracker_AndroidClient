package es.uniovi.eii.contacttracker.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.model.LocationAlarm
import es.uniovi.eii.contacttracker.room.daos.LocationAlarmDao
import es.uniovi.eii.contacttracker.util.DateUtils
import java.util.Date
import javax.inject.Inject

/**
 * Repositorio de Alarmas de Localización.
 *
 * Contiene las operaciones relacionadas con las alarmas de localización y el
 * rastreo automático de ubicación.
 */
class AlarmRepository @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val locationAlarmDao: LocationAlarmDao
) {

    /**
     * Referencia a las shared Prefs
     */
    private val sharedPrefs = ctx.getSharedPreferences(
        ctx.getString(R.string.shared_prefs_file_name), Context.MODE_PRIVATE)


    /**
     * Inserta en la BDD una nueva alarma de localización. Devuelve
     * el ID de la alarma insertada.
     *
     * @param locationAlarm alarma de localización.
     * @return ID de la alarma insertada.
     */
    suspend fun insertLocationAlarm(locationAlarm: LocationAlarm): Long {
        return locationAlarmDao.insert(locationAlarm)
    }

    /**
     * Actualiza los campos de la alarma cuyo ID coincide con el ID de
     * la alarma pasada como parámetro.
     *
     * @param locationAlarm Alarma con los nuevos datos a actualizar.
     * @return N.º de filas modificadas.
     */
    suspend fun updateLocationAlarm(locationAlarm: LocationAlarm): Int {
        return locationAlarmDao.update(locationAlarm)
    }

    /**
     * Devuelve un LiveData con la lista de todas las alarmas
     * programadas por el usuario.
     */
    fun getAllAlarms(): LiveData<List<LocationAlarm>> {
        return locationAlarmDao.getAll()
    }

    /**
     * Devuelve la alarma de localización cuyo ID coincide
     * con el ID pasado como parámetro. Devuelve NULL si no existe.
     *
     * @param id ID de la alarma de localización.
     * @return alarma de localización o NULL si no existe.
     */
    suspend fun getAlarmByID(id: Long): LocationAlarm? {
        return locationAlarmDao.getByID(id)
    }

    /**
     * Elimina todas las alarmas programadas
     * por el usuario y devuelve el nº de alarmas eliminadas.
     */
    suspend fun deleteAllAlarms(): Int {
        return locationAlarmDao.deleteAll()
    }

    /**
     * Elimina la alarma de ID pasado como parámetro.
     *
     * @param id ID de la alarma a eliminar.
     */
    suspend fun deleteAlarmByID(id: Long) {
        locationAlarmDao.deleteById(id)
    }

    /**
     * Devuelve una lista con las alarmas de localización que generan
     * colisión en el tiempo con la alarma pasada como parámetro.
     *
     * @param alarm nueva alarma de localización.
     * @return lista de alarmas de localización que generan colisión.
     */
    suspend fun getAlarmCollisions(alarm: LocationAlarm): List<LocationAlarm> {
        val stringStartDate = DateUtils.formatDate(alarm.startDate, "yyyy-MM-dd HH:mm:ss")
        val stringEndDate = DateUtils.formatDate(alarm.endDate, "yyyy-MM-dd HH:mm:ss")
        return locationAlarmDao.getCollisions(stringStartDate, stringEndDate)
    }

    /**
     * Actualiza el estado de la alarma pasada como
     * parámetro para activarla o desactivarla.
     *
     * @param alarm Alarma de localización.
     * @param enable flag para activar o desactivar.
     * @return Nº de filas modificadas.
     */
    suspend fun toggleAlarm(alarm: LocationAlarm, enable: Boolean): Int {
        val alarmID = alarm.id ?: return 0
        return locationAlarmDao.toggleState(alarmID, enable)
    }

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
     * Guarda en una SharedPreference el flag correspondiente
     * que indica si el rastreo automático está habilitado o deshabilitado.
     *
     * @param flag true si está habilitado.
     */
    fun setAutoTracking(flag: Boolean) {
        with(sharedPrefs.edit()){
            putBoolean(ctx.getString(R.string.shared_prefs_location_alarm_enabled), flag)
            apply()
        }
    }

    /**
     * Devuelve el valor de la clave del flag de rastreo
     * automático.
     */
    fun getAutoTracking(): Boolean {
        return sharedPrefs
                .getBoolean(ctx.getString(R.string.shared_prefs_location_alarm_enabled), false)
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

    /**
     * Elimina de las SharedPreferences las alarmas de inicio y
     * de fin de rastreo de ubicación.
     */
    fun removeAlarms(){
        with(sharedPrefs.edit()){
            remove(ctx.getString(R.string.shared_prefs_location_alarm_start))
            remove(ctx.getString(R.string.shared_prefs_location_alarm_end))
            apply()
        }
    }

}