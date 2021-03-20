package es.uniovi.eii.contacttracker.repositories

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.uniovi.eii.contacttracker.location.services.LocationForegroundService
import es.uniovi.eii.contacttracker.model.UserLocation

/**
 * Repositorio de Localización, que contiene todas las operaciones
 * y funcionalidades relacionadas con los servicios de ubicación.
 */
class LocationRepository {

    /**
     * Localizaciones de usuario.
     */
    private val _userLocations = MutableLiveData<List<UserLocation>>()
    val userLocations: LiveData<List<UserLocation>> = _userLocations


    /**
     * Método encargado de iniciar o detener el servicio de localización, que será
     * ejecutado en segundo plano y hará uso del Tracker de ubicación. Recibe como parámetro
     * el Contexto y también la acción a realizar: START / STOP
     *
     * @param ctx Contexto de Android.
     * @param acción a realizar por el servicio (START/STOP)
     */
    fun toggleLocationService(ctx: Context, action: String){
        Intent(ctx, LocationForegroundService::class.java).let {
            it.action = action
            ContextCompat.startForegroundService(ctx, it)
        }
    }

}