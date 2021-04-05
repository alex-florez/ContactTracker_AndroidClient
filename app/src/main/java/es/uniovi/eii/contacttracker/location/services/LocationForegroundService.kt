package es.uniovi.eii.contacttracker.location.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.App
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.activities.MainActivity
import es.uniovi.eii.contacttracker.fragments.tracklocation.TrackerFragment
import es.uniovi.eii.contacttracker.location.LocationTrackRequest
import es.uniovi.eii.contacttracker.location.LocationUpdateMode
import es.uniovi.eii.contacttracker.location.listeners.callbacks.LocationUpdateCallback
import es.uniovi.eii.contacttracker.location.listeners.intents.LocationReceivedIntentService
import es.uniovi.eii.contacttracker.location.trackers.LocationTracker
import es.uniovi.eii.contacttracker.repositories.AlarmRepository
import es.uniovi.eii.contacttracker.repositories.TrackerSettingsRepository
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.min

/**
 * Servicio Foreground en 1er Plano que realiza el rastreo de ubicación,
 * haciendo uso de uno de los trackers de localización.
 */
@AndroidEntryPoint
class LocationForegroundService : Service(){

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
     * Repositorio de alarmas
     */
    @Inject lateinit var alarmRepository: AlarmRepository

    /**
     * Repositorio de parámetros de configuración del Tracker.
     */
    @Inject lateinit var trackerSettingsRepository: TrackerSettingsRepository


    /**
     * Objeto Notification que representa la notificación
     * que se mostrará al iniciar el servicio.
     */
    private lateinit var notification: Notification

    /**
     * Flag que indica si ya se está ejecutando el servicio.
     */
    private var isActive: Boolean = false

    override fun onCreate() {
        super.onCreate()
        init()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    // COMIENZO DEL SERVICIO
    // *********************
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            // Flag que indica si el comando es enviado desde una alarma de localización.
            val commandFromAlarm = it.getBooleanExtra(EXTRA_COMMAND_FROM_ALARM, false)
            when(it.action){
                ACTION_START_LOCATION_SERVICE -> {
                    startLocationService(commandFromAlarm)
                }
                ACTION_STOP_LOCATION_SERVICE -> {
                    stopLocationService(commandFromAlarm)
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
     * ha sido ya inicializado. Recibe como parámetro un flag que indica si
     * el servicio ha sido iniciado desde una alarma de localización.
     *
     * @param commandFromAlarm flag de comando desde alarma.
     */
    private fun startLocationService(commandFromAlarm: Boolean){
        if(!isActive){
            Log.d(TAG, "Iniciando servicio de localización en 1er plano.")
            startForeground(SERVICE_ID, notification)
            locationTracker.setLocationRequest(createLocationTrackRequest()) // Construir LocationTrackRequest
            locationTracker.start(LocationUpdateMode.CALLBACK_MODE)
            isActive = true
            if(commandFromAlarm){ // Si es ejecutado desde una alarma
                sendBroadcast(ACTION_START_LOCATION_SERVICE)
            }
        }
    }

    /**
     * Se encarga de eliminar y detener las actualizaciones de localización,
     * así como también detener el servicio.Recibe como parámetro un flag que indica si
     * el servicio ha sido iniciado desde una alarma de localización.
     *
     * @param commandFromAlarm flag de comando desde alarma.
     */
    private fun stopLocationService(commandFromAlarm: Boolean){
        if(isActive){
            locationTracker.stop(LocationUpdateMode.CALLBACK_MODE)
            stopForeground(true)
            stopSelf()
            isActive = false
            if(commandFromAlarm){ // Si es ejecutado desde una alarma.
                alarmRepository.removeAlarms() // Eliminar alarmas de las Shared Prefs
                sendBroadcast(ACTION_STOP_LOCATION_SERVICE)
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
        return NotificationCompat.Builder(this, App.CHANNEL_ID_LOCATION_FOREGROUND_SERVICE)
                .setContentTitle("Servicio de localización")
                .setContentText("Rastreando tu ubicación...")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setTicker("Ticker!")
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
        val minInterval = trackerSettingsRepository.getMinInterval()
        return LocationTrackRequest(
                minInterval = minInterval,
                fastestInterval = minInterval)
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


    companion object {
        // Constantes
        private const val TAG = "LocationForegroundService"

        /**
         * ID del servicio.
         */
        private const val SERVICE_ID = 1000

        // ACCIONES
        const val ACTION_START_LOCATION_SERVICE = "startLocationService" // Iniciar servicio
        const val ACTION_STOP_LOCATION_SERVICE = "stopLocationService" // Detener servicio

        // EXTRAS
        const val EXTRA_COMMAND_FROM_ALARM = "commandFromAlarm" // Flag que indica si el comando procede de una alarma
    }
}