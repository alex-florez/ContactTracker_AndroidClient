package es.uniovi.eii.contacttracker.location.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.App
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.activities.MainActivity
import es.uniovi.eii.contacttracker.fragments.tracklocation.TrackerFragment
import es.uniovi.eii.contacttracker.location.LocationTrackRequest
import es.uniovi.eii.contacttracker.location.LocationUpdateMode
import es.uniovi.eii.contacttracker.location.alarms.LocationAlarmManager
import es.uniovi.eii.contacttracker.location.listeners.callbacks.LocationUpdateCallback
import es.uniovi.eii.contacttracker.location.listeners.intents.LocationReceivedIntentService
import es.uniovi.eii.contacttracker.location.trackers.LocationTracker
import es.uniovi.eii.contacttracker.repositories.ConfigRepository
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.util.PermissionUtils
import es.uniovi.eii.contacttracker.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject
import javax.inject.Named

/**
 * Servicio Foreground en 1er Plano que realiza el rastreo de ubicación,
 * haciendo uso de uno de los trackers de localización.
 */
@AndroidEntryPoint
class LocationForegroundService : Service(){

    /**
     * Scope para las corrutinas.
     */
    val scope = CoroutineScope(Job() + Dispatchers.IO)

    /**
     * Rastreador de ubicación (LocationTracker)
     */
    @Inject
    @Named("fused_location")
    lateinit var locationTracker: LocationTracker

    /**
     * Callback de localización.
     */
    @Inject
    lateinit var locationCallback: LocationUpdateCallback

    /**
     * Repositorio de configuración.
     */
    @Inject lateinit var configRepository: ConfigRepository


    /**
     * Manager para las ALARMAS DE LOCALIZACIÓN.
     */
    @Inject lateinit var locationAlarmManager: LocationAlarmManager

    /**
     * Objeto Notification que representa la notificación
     * que se mostrará al iniciar el servicio.
     */
    private lateinit var notification: Notification

    /**
     * Flag que indica si ya se está ejecutando el servicio.
     */
    private var isActive: Boolean = false

    /**
     * Referencia a las Shared Preferences.
     */
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(getString(R.string.shared_prefs_file_name), MODE_PRIVATE)
        init()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    // COMIENZO DEL SERVICIO
    // *********************
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            // ID de la alarma que inició el servicio (-1 si es inicio manual)
            val alarmID = it.getLongExtra(Constants.EXTRA_LOCATION_ALARM_ID, -1L)
            when(it.action){
                Constants.ACTION_START_LOCATION_SERVICE -> {
                    startLocationService(alarmID)
                }
                Constants.ACTION_STOP_LOCATION_SERVICE -> {
                    stopLocationService(alarmID)
                }
            }
        }
        return START_STICKY
    }

    // FIN DEL SERVICIO
    // ****************
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Destruyendo servicio de localización en 1er plano.")
    }

    /**
     * Método de inicialización que se encarga de instanciar
     * y configurar los objetos necesarios para este servicio.
     */
    private fun init(){
        notification = createNotification()
        locationTracker.setCallback(locationCallback)
        val pendingIntent = Intent(this, LocationReceivedIntentService::class.java).let{
            PendingIntent.getService(this, 0, it, 0)
        }
        locationTracker.setIntentCallback(pendingIntent)
    }

    /**
     * Inicializa el servicio de localización en 1er plano, si no
     * ha sido ya inicializado. Recibe como parámetro el ID de la alarma
     * de localización que inició el servicio o -1 si es manual.
     *
     * @param alarmID ID de la alarma de localización o -1.
     */
    private fun startLocationService(alarmID: Long){
        if(!isActive){
            // Comprobar configuración de localización
            if(!checkLocationSettings()) {
                // Desactivar alarma
                locationAlarmManager.toggleAlarm(alarmID, false)
            } else {
                Log.d(TAG, "Iniciando servicio de localización en 1er plano.")
                startForeground(SERVICE_ID, notification)
                locationTracker.setLocationRequest(createLocationTrackRequest()) // Construir LocationTrackRequest
                locationTracker.start(LocationUpdateMode.CALLBACK_MODE)
                isActive = true
                if(alarmID != -1L){ // Si es ejecutado desde una alarma de localización
                    sendBroadcast(Constants.ACTION_START_LOCATION_SERVICE) // Broadcast para actualizar la UI.
                }
            }
        }
    }

    /**
     * Se encarga de eliminar y detener las actualizaciones de localización,
     * así como también detener el servicio.

     * @param alarmID ID de la alarma de localización o -1 si es manual.
     */
    private fun stopLocationService(alarmID: Long){
        if(isActive){
            locationTracker.stop(LocationUpdateMode.CALLBACK_MODE)
            stopForeground(true)
            stopSelf()
            isActive = false
            if(alarmID != -1L){ // Si es ejecutado desde una alarma.
                sendBroadcast(Constants.ACTION_STOP_LOCATION_SERVICE) // Enviar broadcast a la UI.
                locationAlarmManager.toggleAlarm(alarmID, false) // Desactivar la alarma
            }
        }
    }

    /**
     * Método privado que se encarga de crear la
     * notificación del ForegroundService.
     *
     * @return objeto Notification ya construido.
     */
    private fun createNotification(): Notification{
        val pendingIntent:PendingIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, 0)
        }
        /* Cuerpo de la notificación */
        val timeInterval = DateUtils.getMinuteSecond(sharedPrefs.getLong(
            applicationContext.getString(R.string.shared_prefs_tracker_config_min_interval), 0L))
        val body = "Tu ubicación está siendo registrada cada ${timeInterval[0]} min ${timeInterval[1]} sec."
        return NotificationCompat.Builder(this, App.CHANNEL_ID_LOCATION_FOREGROUND_SERVICE)
                .setContentTitle(applicationContext.getString(R.string.locationServiceTitle))
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setTicker("Ticker!")
                .setColorized(true)
                .setColor(getColor(R.color.blue1))
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .build()
    }

    /**
     * Obtiene del repositorio de configuración del Tracker,
     * los parámetros de configuración y construye el objeto
     * LocationTrackRequest con dichos parámetros.
     *
     * @return objeto de solicitud de rastreo de ubicación.
     */
    private fun createLocationTrackRequest(): LocationTrackRequest {
        /* Recuperar configuración de rastreo */
        val config = configRepository.getTrackerConfig()
        return LocationTrackRequest(
                minInterval = config.minInterval,
                fastestInterval = config.minInterval,
                smallestDisplacement = config.smallestDisplacement)
    }

    /**
     * Método encargado de enviar un Broadcast indicando el
     * comando de INICIO o de FIN de la alarma de localización.
     *
     * @param command Comando de INICIO o de FIN.
     */
    private fun sendBroadcast(command: String) {
        val intent = Intent()
        intent.action = TrackerFragment.ACTION_LOCATION_ALARM
        intent.putExtra(TrackerFragment.EXTRA_LOCATION_ALARM_COMMAND, command)
        sendBroadcast(intent)
    }

    /**
     * Devuelve true si los ajustes de localización
     * son correctos.
     */
    private fun checkLocationSettings(): Boolean {
        return PermissionUtils.check(applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                &&
                LocationUtils.checkGPS(applicationContext)
    }

    companion object {
        // Constantes
        private const val TAG = "LocationForegroundService"

        /**
         * ID del servicio.
         */
        private const val SERVICE_ID = 1000
    }
}