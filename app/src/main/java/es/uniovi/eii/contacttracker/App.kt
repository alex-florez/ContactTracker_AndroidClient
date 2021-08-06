package es.uniovi.eii.contacttracker

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.location.Location
import android.os.Build
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.util.FileUtils.readFile
import es.uniovi.eii.contacttracker.util.LocationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import javax.inject.Inject
import javax.inject.Scope

/**
 * Clase App que extiende de Application y que engloba
 * al ciclo de vida completo de la aplicación.
 */
@HiltAndroidApp
class App : Application() {

    companion object {
        /* CANALES PARA LAS NOTIFICACIONES */
        const val CHANNEL_ID_LOCATION_FOREGROUND_SERVICE = "LocationForegroundServiceChannel"
        const val CHANNEL_ID_RISK_CONTACT_RESULT = "RiskContactResultChannel"
        const val CHANNEL_ID_RISK_CONTACT_CHECKING = "RiskContactCheckingChannel"
    }

    @Inject
    lateinit var repo: LocationRepository

    var scope = CoroutineScope(Job() + Dispatchers.IO)

    private var positive = false

    /**
     * Referencia a las Shared Preferences.
     */
    private lateinit var sharedPrefs: SharedPreferences


    override fun onCreate() {
        super.onCreate()
        deleteDatabase("contacttracker.db")
        sharedPrefs = getSharedPreferences(getString(R.string.shared_prefs_file_name), MODE_PRIVATE)
        createNotificationChannels()
        initSharedPrefs()
        simulate()
    }


    /**
     * Crea los canales de notificación si la App se
     * está ejecutando en versiones Oreo o superior.
     */
    private fun createNotificationChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Canal para el ForegroundService de localización.
            val locationFSChannel = NotificationChannel(
                    CHANNEL_ID_LOCATION_FOREGROUND_SERVICE,
                    "Location Foreground Service Channel",
                    NotificationManager.IMPORTANCE_HIGH)

            // Canal para los resultados de la comprobación de contactos de riesgo
            val riskContactsResultChannel = NotificationChannel(
                    CHANNEL_ID_RISK_CONTACT_RESULT,
                    "Risk Contact Result Channel",
                    NotificationManager.IMPORTANCE_HIGH)

            // Canal para el servicio en 1er plano de la comprobación de los contactos de riesgo
            val riskContactCheckingChannel = NotificationChannel(
                CHANNEL_ID_RISK_CONTACT_CHECKING,
                "Risk Contact Checking Channel",
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(locationFSChannel)
            notificationManager.createNotificationChannel(riskContactsResultChannel)
            notificationManager.createNotificationChannel(riskContactCheckingChannel)
        }
    }

    /**
     * Inicializa las propiedades necesarias de las Shared Preferences
     * si aún no existen.
     */
    private fun initSharedPrefs(){
        with(sharedPrefs.edit()){
            /* Intervalo de tiempo mínimo */
            if(!sharedPrefs.contains(getString(R.string.shared_prefs_tracker_config_min_interval))){
                putLong(getString(R.string.shared_prefs_tracker_config_min_interval), 3000)
            }
            /* Desplazamiento mínimo */
            if(!sharedPrefs.contains(getString(R.string.shared_prefs_tracker_config_smallest_displacement))){
                putInt(getString(R.string.shared_prefs_tracker_config_smallest_displacement), 0)
            }
            /* Alcance de la comprobación */
            if(!sharedPrefs.contains(getString(R.string.shared_prefs_risk_contact_check_scope))){
                putInt(getString(R.string.shared_prefs_risk_contact_check_scope), 3)
            }
            apply()
        }
    }


    private fun simulate() {
        if(positive){
            val positiveLocations = LocationUtils.parseLocationsFile("positive.txt", this)
            scope.launch {
                positiveLocations.forEach {
                    repo.insertUserLocation(it)
                }
            }
        } else {
            val locations = LocationUtils.parseLocationsFile("user.txt", this)
            scope.launch {
                if(repo.getAllUserLocationsList().isEmpty()) {
                    locations.forEach {
                        repo.insertUserLocation(it)
                    }
                }
            }
        }
    }

}