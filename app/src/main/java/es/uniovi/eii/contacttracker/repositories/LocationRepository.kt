package es.uniovi.eii.contacttracker.repositories

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import es.uniovi.eii.contacttracker.location.alarms.LocationAlarm
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.room.daos.LocationAlarmDao
import es.uniovi.eii.contacttracker.room.daos.UserLocationDao
import es.uniovi.eii.contacttracker.util.DateUtils
import java.util.*
import javax.inject.Inject

/**
 * Repositorio de Datos de Localización.
 *
 * Contiene todas las operaciones y funcionalidades relacionadas con
 * el almacenamiento y obtención de localizaciones proporcionadas por el tracker,
 * además de las operaciones relacionadas con las Alarmas de Localización.
 */
class LocationRepository @Inject constructor(
    private val userLocationDao: UserLocationDao,
    private val locationAlarmDao: LocationAlarmDao
) {

    /**
     * Inserta en la base de datos la localización de
     * usuario pasada como parámetro.
     *
     * @return id de la fila insertada
     */
    suspend fun insertUserLocation(userLocation: UserLocation): Long {
       return userLocationDao.insert(userLocation)
    }

    /**
     *  Devuelve todas las localizaciones del usuario
     *  registradas hasta la fecha.
     */
    fun getAllUserLocations(): LiveData<List<UserLocation>>{
        return userLocationDao.getAll()
    }

    /**
     * Recibe como parámetro una fecha en formato YYYY-MM-DD y devuelve
     * todas las localizaciones del usuario que hayan sido registradas
     * en esa fecha.
     *
     * @param dateString fecha en formato YYYY-MM-DD.
     * @return LiveData con la lista de localizaciones.
     */
    fun getAllUserLocationsByDate(dateString: String): LiveData<List<UserLocation>> {
        return userLocationDao.getAllByDateString(dateString)
    }

    /**
     * Devuelve una lista con todas las localizaciones de usuario registradas en
     * la base de datos.
     *
     * @return Listado de localizaciones de usuario.
     */
    fun getAllUserLocationsList(): List<UserLocation> = userLocationDao.getAllList()

    /**
     * Elimina todas las localizaciones de usuario cuya fecha coincide
     * con la fecha pasada como parámetro.
     *
     * @param dateString Fecha por la que filtrar.
     */
    suspend fun deleteUserLocationsByDate(dateString: String): Int {
        return userLocationDao.deleteByDate(dateString)
    }

    /**
     * Devuelve las localizaciones del usuario registradas desde la fecha pasada
     * como parámetro hasta el día de hoy.
     *
     * @param lastDays N.º de días atrás para consultar las localizaciones.
     * @return lista con las localizaciones.
     */
    suspend fun getLastLocationsSince(lastDays: Int): List<UserLocation>{
        val sinceDate = DateUtils.addToDate(Date(), Calendar.DATE, -1 * lastDays)
        return userLocationDao.getLocationsBetween(
            DateUtils.formatDate(sinceDate, "yyyy-MM-dd"),
            DateUtils.formatDate(Date(), "yyyy-MM-dd"))
    }

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
     *
     * @return LiveData que contiene el listado de alarmas de localización.
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
}