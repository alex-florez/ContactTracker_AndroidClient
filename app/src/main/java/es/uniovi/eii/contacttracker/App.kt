package es.uniovi.eii.contacttracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import es.uniovi.eii.contacttracker.repositories.LocationRepository

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
    }


    override fun onCreate() {
        super.onCreate()
        applicationContext.deleteDatabase("contacttracker.db")
        createNotificationChannels()
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

            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(locationFSChannel)
            notificationManager.createNotificationChannel(riskContactsResultChannel)
        }
    }

}