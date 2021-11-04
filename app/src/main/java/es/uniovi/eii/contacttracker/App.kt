package es.uniovi.eii.contacttracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.content.edit
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.HiltAndroidApp
import es.uniovi.eii.contacttracker.network.model.APIResult
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.repositories.StatisticsRepository
import es.uniovi.eii.contacttracker.util.LocationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.Date



/**
 * Clase App que extiende de Application y que engloba
 * al ciclo de vida completo de la aplicación.
 */
@HiltAndroidApp
class App : Application() {

    companion object {
        /* TAG de la aplicación */
        private const val TAG = "ContactTrackerApp"
        /* CANALES PARA LAS NOTIFICACIONES */
        const val CHANNEL_ID_LOCATION_FOREGROUND_SERVICE = "LocationForegroundServiceChannel"
        const val CHANNEL_ID_RISK_CONTACT_RESULT = "RiskContactResultChannel"
        const val CHANNEL_ID_RISK_CONTACT_CHECKING = "RiskContactCheckingChannel"
    }

    /* Scope para las corrutinas con Dispatacher de IO */
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    @Inject
    lateinit var locationRepository: LocationRepository

    /* Nombre del fichero desde el que se cargan las localizaciones de la simulación */
    private val simulationFilename = "user.txt"

    /**
     * Referencia a las Shared Preferences.
     */
    private lateinit var sharedPrefs: SharedPreferences

    /* Repositorio de Estadísticas */
    @Inject lateinit var statisticsRepository: StatisticsRepository

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(getString(R.string.shared_prefs_file_name), MODE_PRIVATE)
        createNotificationChannels()
        initSharedPrefs()
        subscribeToTopics()
        simulate(simulationFilename, Triple(2021, 10, 1))
        registerInstall()
    }


    /**
     * Crea los canales para las notificaciones si la App se está
     * ejecutando en versiones Oreo o superior.
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

            // Canal para las notificaciones FCM
            val fcmChannel = NotificationChannel(
                getString(R.string.fcm_channel_id),
                "FCM Notifications Channel",
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(locationFSChannel)
            notificationManager.createNotificationChannel(riskContactsResultChannel)
            notificationManager.createNotificationChannel(riskContactCheckingChannel)
            notificationManager.createNotificationChannel(fcmChannel)
        }
    }

    /**
     * Inicializa las propiedades necesarias de las Shared Preferences
     * si aún no existen.
     */
    private fun initSharedPrefs(){

        sharedPrefs.edit {
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
            /* Flag de 1a vez iniciada la aplicación */
            if(!sharedPrefs.contains(getString(R.string.shared_prefs_new_install))) {
                putBoolean(getString(R.string.shared_prefs_new_install), true)
            }
        }
    }

    /**
     * Subscribe el dispositivo a los tópicos / temas de Firebase Cloud Messaging
     * para recibir notificaciones que se envíen a esos temas, siempre y cuando
     * la Preferencia de para enviar esas notificaciones esté habilitada.
     */
    private fun subscribeToTopics() {
        /* Notificaciones acerca del n.º de positivos notificados */
        if(sharedPrefs.getBoolean(getString(R.string.shared_prefs_positives_notifications), false)) {
            Firebase.messaging.subscribeToTopic("positives")
                .addOnCompleteListener {
                    var msg = "Cliente Android suscrito al Topic 'positives'."
                    if(!it.isSuccessful)
                        msg = "Error al suscribir el cliente Android en el tema."
                    Log.d("FCM", msg)
                }
        }
    }

    /**
     * Si es la 1a vez que se inicia la aplicación se registra una nueva
     * instalación en el backend. Realiza una llamada a la API REST de
     * estadísticas.
     */
    private fun registerInstall() {
        scope.launch {
            when(val response = statisticsRepository.registerNewInstall(Date().time)) {
                is APIResult.Success -> {
                    Log.d(TAG, response.value.msg)
                }
                else -> {
                    Log.d(TAG, "No se ha podido registrar la instalación.")
                }
            }
        }
    }

    /**
     * Carga las localizaciones del fichero de nombre pasado como
     * parámetro y las almacena en la base de datos local del
     * dispositivo, siempre y cuando no hayan sido ya cargadas. Esto
     * se comprueba a través de un flag almacenado en las SharedPreferences.
     *
     * @param filename Nombre del fichero.
     * @param date Fecha con el año, mes (0-11) y día de las localizaciones.
     */
    private fun simulate(filename: String, date: Triple<Int, Int, Int>? = null) {
        // Comprobar el flag de las shared preferences
        if(!sharedPrefs.getBoolean(Constants.SIMULATION_LOCATIONS_LOADED, false)) {
            val locations = LocationUtils.parseLocationsFile(filename, this, date)
            scope.launch {
                locations.forEach {
                    locationRepository.insertUserLocation(it)
                }
            }
            // Activar flag de que ya se han cargado las localizciones de la simulación.
            sharedPrefs.edit {
                putBoolean(Constants.SIMULATION_LOCATIONS_LOADED, true)
            }
        }
    }

}