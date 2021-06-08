package es.uniovi.eii.contacttracker.repositories

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import es.uniovi.eii.contacttracker.model.Itinerary
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.room.daos.UserLocationDao
import es.uniovi.eii.contacttracker.util.Utils
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

/**
 * Repositorio de Localización.
 *
 * Contiene todas las operaciones y funcionalidades relacionadas
 * con los servicios de ubicación y obtención de localizaciones almacenadas.
 */
class LocationRepository @Inject constructor(
    private val userLocationDao: UserLocationDao
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
     * Elimina todas las localizaciones del usuario de
     * la base de datos y devuelve el número de filas eliminadas.
     *
     * @return nº de filas eliminadas.
     */
    suspend fun deleteAllUserLocations(): Int {
       return userLocationDao.deleteAll()
    }

    /**
     * Elimina todas las localizaciones de usuario cuya fecha coincide
     * con la fecha pasada como parámetro.
     *
     * @param dateString fecha por la que filtrar.
     */
    suspend fun deleteUserLocationsByDate(dateString: String): Int {
        return userLocationDao.deleteByDate(dateString)
    }

    /**
     * Devuelve todas las localizaciones del usuario registradas en
     * el día de HOY.
     *
     * @return lista con las localizaciones de hoy.
     */
    suspend fun getTodayUserLocations(): List<UserLocation> {
        return userLocationDao.getToday()
    }

    /**
     * Devuelve las localizaciones del usuario registradas desde la fecha pasada
     * como parámetro hasta el día de hoy.
     *
     * @param dateString string formateado con la fecha de inicio.
     * @return lista con las localizaciones.
     */
    suspend fun getLastLocationsSince(dateString: String): List<UserLocation>{
        return userLocationDao.getLocationsBetween(dateString, Utils.formatDate(Date(), "yyyy-MM-dd"))
    }

    /**
     * Devuelve una lista de fechas sin repeticiones que se corresponden
     * con las fechas de las últimas localizaciones registradas desde la fecha
     * pasada como parámetro hasta el día de hoy.
     *
     * @param dateString String con la fecha formateada.
     * @return lista con los string de fechas formateadas.
     */
    @SuppressLint("SimpleDateFormat")
    suspend fun getLastLocationDatesSince(dateString: String): List<String> {
        return userLocationDao.getLocationDatesBetween(dateString,  Utils.formatDate(Date(), "yyyy-MM-dd"))
    }

    /**
     * Devuelve el itinerario de localizaciones asociadas desde la
     * fecha pasada como parámetro hasta día de hoy.
     *
     * @param dateString fecha en formato yyyy-mm-dd.
     * @return Itinerario con las localizaciones organizadas por fecha.
     */
    suspend fun getItinerarySince(dateString: String): Itinerary {
        val map: MutableMap<String, List<UserLocation>> = mutableMapOf()
        val dates: List<String> = userLocationDao.getLocationDatesBetween(dateString, Utils.formatDate(Date(), "yyyy-MM-dd"))
        // Rellenar el mapa
        dates.forEach{ date ->
            val locations = userLocationDao.getAllByDate(date)
            map[date] = locations
        }
        return Itinerary(map)
    }
}