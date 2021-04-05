package es.uniovi.eii.contacttracker.repositories

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.uniovi.eii.contacttracker.location.services.LocationForegroundService
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.room.AppDatabase
import es.uniovi.eii.contacttracker.room.daos.UserLocationDao
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

    fun insert(userLocation: UserLocation){
        userLocationDao.insertLoc(userLocation)
    }

}