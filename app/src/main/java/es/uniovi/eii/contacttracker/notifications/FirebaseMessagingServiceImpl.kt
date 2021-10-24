package es.uniovi.eii.contacttracker.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.activities.MainActivity

/**
 * Servicio para recibir los mensajes de Firebase Cloud Messaging.
 */
class FirebaseMessagingServiceImpl : FirebaseMessagingService() {

    /**
     * Método invocado si el token de registro del dispositivo es actualizado.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Nuevo token de registro recibido: $token")
    }

    /**
     * Método invocado cuando se recibe un nuevo mensaje desde FCM
     * y la aplicación está en primer plano.
     *
     * @param msg Objeto con el mensaje de FCM.
     */
    override fun onMessageReceived(msg: RemoteMessage) {
        Log.d(TAG, "Enviado desde: ${msg.from}")

        // Carga de datos (Data Payload)
        if(msg.data.isNotEmpty()) {
            Log.d(TAG, "Datos: ${msg.data}")
        }
        // Carga de notificación (Notification Payload)
        msg.notification?.let {
            Log.d(TAG, "Cuerpo de la notificación: ${it.body}")
            // Mostrar una notificación
            showNotification(msg)
        }
    }


    /**
     * Muestra una notificación con el título y el cuerpo del mensaje
     * recibido de FCM, cuando la aplicación está en 1er plano.
     */
    private fun showNotification(msg: RemoteMessage) {
        val channelId = getString(R.string.fcm_channel_id)
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        Log.d(TAG, msg.notification?.title.toString())
        // Crear notificación
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(msg.notification?.title)
            .setContentText(msg.notification?.body)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .setStyle(NotificationCompat.BigTextStyle().bigText(msg.notification?.body))
            .build()

        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(0, notification)
    }

    companion object {
        private const val TAG = "FirebaseMessagingServiceImpl"
    }
}