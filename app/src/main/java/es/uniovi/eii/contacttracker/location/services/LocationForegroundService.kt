package es.uniovi.eii.contacttracker.location.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import es.uniovi.eii.contacttracker.App
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.activities.MainActivity
import es.uniovi.eii.contacttracker.location.LocationUpdateMode
import es.uniovi.eii.contacttracker.location.listeners.callbacks.BroadcastLocationCallback
import es.uniovi.eii.contacttracker.location.listeners.callbacks.LocationUpdateCallback
import es.uniovi.eii.contacttracker.location.listeners.callbacks.LogLocationCallback
import es.uniovi.eii.contacttracker.location.listeners.intents.LocationReceivedIntentService
import es.uniovi.eii.contacttracker.location.trackers.FusedLocationTracker
import es.uniovi.eii.contacttracker.location.trackers.LocationTracker

/**
 * Servicio en 1er Plano que realiza el rastreo de ubicación,
 * haciendo uso de uno de los trackers de localización.
 */
class LocationForegroundService : Service(){

    /**
     * Rastreador de ubicación (LocationTracker)
     */
    private lateinit var locationTracker: LocationTracker

    /**
     * Callback de localización.
     */
    private lateinit var locationCallback: LocationUpdateCallback

    /**
     * Objeto Notification que representa la notificación
     * que se mostrará al iniciar el servicio.
     */
    private lateinit var notification: Notification

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
            when(it.action){
                ACTION_START_LOCATION_SERVICE -> {
                    startLocationService()
                }
                ACTION_STOP_LOCATION_SERVICE -> {
                    stopLocationService()
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
     * Inicializa el servicio de localización en 1er plano.
     */
    private fun startLocationService(){
        Log.d(TAG, "Iniciando servicio de localización en 1er plano.")
        startForeground(SERVICE_ID, notification)
        locationTracker.start(LocationUpdateMode.CALLBACK_MODE)
    }

    /**
     * Se encarga de eliminar y detener las actualizaciones de localización,
     * así como también detener el servicio.
     */
    private fun stopLocationService(){
        locationTracker.stop(LocationUpdateMode.CALLBACK_MODE)
        stopForeground(true)
        stopSelf()
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
     * Método de inicialización que se encarga de instanciar
     * y configurar los objetos necesarios para este servicio.
     */
    private fun init(){
        locationTracker = FusedLocationTracker(this)
        locationCallback = BroadcastLocationCallback(this)
        notification = createNotification()
        locationTracker.setCallback(locationCallback)
        val pendingIntent = Intent(this, LocationReceivedIntentService::class.java).let{
            PendingIntent.getService(this, 0, it, 0)
        }
        locationTracker.setIntentCallback(pendingIntent)
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
    }
}