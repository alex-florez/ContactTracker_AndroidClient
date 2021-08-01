package es.uniovi.eii.contacttracker.riskcontact.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.App
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.riskcontact.RiskContactManager
import kotlinx.coroutines.*
import javax.inject.Inject

/* ID del Servicio */
private const val SERVICE_ID = 1001

/**
 * Servicio de Android en 1er Plano en el que se ejecuta la
 * comprobación de contactos de riesgo.
 */
@AndroidEntryPoint
class RiskContactForegroundService : Service() {

    /**
     * Manager para los contactos de riesgo.
     */
    @Inject
    lateinit var riskContactManager: RiskContactManager

    /**
     * Notificación del Servicio
     */
    private lateinit var notification: Notification

    /**
     * Scope para la corrutina
     */
    private val scope = CoroutineScope(Job() + Dispatchers.IO)


    override fun onCreate() {
        super.onCreate()
        notification = createNotification()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(SERVICE_ID, notification)
        scope.launch {
            riskContactManager.checkRiskContacts() // Iniciar comprobación
            stopForeground(true)
            stopSelf()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }


    /**
     * Crea y configura una notificación Android para mostrarla
     * mientras el servicio está en ejecución.
     */
    private fun createNotification(): Notification {
        val text = "Comparando tus localizaciones con las de otros positivos registrados en la nube..."
        return NotificationCompat.Builder(this, App.CHANNEL_ID_RISK_CONTACT_CHECKING)
            .setContentTitle("Comprobación de contactos de riesgo")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
    }
}