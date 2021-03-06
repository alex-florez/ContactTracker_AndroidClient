package es.uniovi.eii.contacttracker.location.listeners.callbacks

import android.content.Context
import android.content.Intent
import android.location.Location
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.location.receivers.LocationUpdateBroadcastReceiver
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.room.AppDatabase
import es.uniovi.eii.contacttracker.util.LocationUtils
import kotlinx.coroutines.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

/**
 * Callback para recibir actualizaciones de localización y
 * registrar estas localizaciones en la Base de datos ROOM.
 *
 * Las localizaciones también son enviadas a un BroadCastReceiver
 * para actualizar la UI donde sea necesario.
 */
class RegisterLocationCallback @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val locationRepository: LocationRepository
) : LocationUpdateCallback {

    /**
     * Scope para la corrutina.
     */
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    /**
     * Referencia a la corrutina.
     */
    private var job: Job? = null

    override fun onLocationUpdate(location: Location) {
        // Enviar BROADCAST
        // ----------------
        sendBroadcast(location)

        // Insertar localización en ROOM
        // -----------------------------
        insertIntoDB(location)
    }

    override fun onLocationStop() {
        // Cancelar el JOB
        job?.cancel()
    }

    /**
     * Envía un BROADCAST con la nueva localización para que
     * sea recibido por todos los BroadcastReceivers que estén
     * interesados en esta acción.
     */
    private fun sendBroadcast(location: Location){
        val intent = Intent()
        intent.action = LocationUpdateBroadcastReceiver.ACTION_GET_LOCATION
        intent.putExtra(LocationUpdateBroadcastReceiver.EXTRA_LOCATION, location)
        ctx.sendBroadcast(intent)
    }

    /**
     * Utiliza una corrutina de Kotlin para insertar la
     * nueva localización en la base de datos ROOM local de la App.
     */
    private fun insertIntoDB(location: Location){
        job = scope.launch {
            locationRepository.insertUserLocation(LocationUtils.parse(location))
        }
    }

}