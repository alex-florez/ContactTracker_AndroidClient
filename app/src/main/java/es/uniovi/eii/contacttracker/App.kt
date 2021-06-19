package es.uniovi.eii.contacttracker

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.location.Location
import android.os.Build
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.util.readFile
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

    override fun onCreate() {
        super.onCreate()
        deleteDatabase("contacttracker.db")
        createNotificationChannels()
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


    private fun simulate() {
        val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
//        /* POSITIVO */
//        val p1 = UserLocation(2222, 43.537562, -5.904420, 20.0, "", df.parse("08/06/2021 12:08:03"))
//
//        /* USUARIO */
//        scope.launch {
//            repo.insertUserLocation(p1)
//        }
        if(positive){
            val positiveLines = readFile(applicationContext, "positive.txt")
            val positiveLocations = mutableListOf<UserLocation>()
            var counter = 0
            positiveLines.forEach {
                val cols = it.split(",")
                val location = UserLocation(counter.toLong(), cols[1].toDouble(), cols[0].toDouble(), 20.0, "", df.parse(cols[2])!!)
                positiveLocations.add(location)
                counter++
            }
            scope.launch {
                positiveLocations.forEach {
                    repo.insertUserLocation(it)
                }
            }
        } else {
            val userLines = readFile(applicationContext, "user.txt")
            val userLocations = mutableListOf<UserLocation>()
            var counter = 0
            userLines.forEach {
                val cols = it.split(",")
                val location = UserLocation(counter.toLong(), cols[1].toDouble(), cols[0].toDouble(), 20.0, "", df.parse(cols[2])!!)
                userLocations.add(location)
                counter++
            }
            scope.launch {
                userLocations.forEach {
                    repo.insertUserLocation(it)
                }
            }
        }

    }

}