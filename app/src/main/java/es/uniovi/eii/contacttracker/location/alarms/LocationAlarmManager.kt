package es.uniovi.eii.contacttracker.location.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.location.receivers.LocationAlarmCommandBroadcastReceiver
import es.uniovi.eii.contacttracker.location.services.LocationForegroundService
import es.uniovi.eii.contacttracker.model.LocationAlarm
import es.uniovi.eii.contacttracker.repositories.AlarmRepository
import es.uniovi.eii.contacttracker.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Clase que representa el Manager para gestionar
 * las alarmas de localización.
 */
class LocationAlarmManager @Inject constructor(
        private val alarmManager: AlarmManager,
        private val alarmRepository: AlarmRepository,
        @ApplicationContext val ctx: Context
) {

    /**
     * Scope para la corrutina.
     */
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    /**
     * Devuelve un LiveData con la lista de todas
     * las alarmas de localización.
     */
    fun getAllAlarms(): LiveData<List<LocationAlarm>> {
        return alarmRepository.getAllAlarms()
    }

    /**
     * Devuelve una alarma de localización a partir de su ID.
     *
     * @param id ID de la alarma de localización.
     * @return alarma de localización.
     */
    suspend fun getAlarmByID(id: Long): LocationAlarm? {
        return alarmRepository.getAlarmByID(id)
    }

    /**
     * Elimina todas las alarmas de localización.
     */
    fun deleteAllAlarms() {
        scope.launch {
            alarmRepository.deleteAllAlarms()
        }
    }

    /**
     * Elimina la alarma cuyo ID coincide con el
     * de la alarma pasada como paráemtro.
     *
     * @param locationAlarm alarma de localización a eliminar.
     */
    fun deleteAlarm(locationAlarm: LocationAlarm) {
        val alarmID = locationAlarm.id ?: return
        scope.launch {
            // Cancelar alarma en el sistema Android.
            cancelAndroidAlarm(locationAlarm)
            // Eliminar alarma de la BDD
            alarmRepository.deleteAlarmByID(alarmID)
        }
    }

    /**
     * Comprueba si existen colisiones entre las alarmas de localización
     * almacenadas y la alarma pasada como parámetro.
     *
     * @param locationAlarm alarma de localización.
     * @return lista con las alarmas con las que existe colisión.
     */
    suspend fun checkAlarmCollisions(locationAlarm: LocationAlarm): List<LocationAlarm> {
        return alarmRepository.getAlarmCollisions(locationAlarm)
    }

    /**
     * Se encarga de establecer una Alarma de Localización.
     * Se inserta en la base de datos y además se configura una
     * nueva alarma en Android.
     *
     * @param locationAlarm alarma de localización.
     */
    fun setAlarm(locationAlarm: LocationAlarm) {
        scope.launch {
            // Actualizar horas de la alarma (si es necesario)
            locationAlarm.updateHours()
            // Insertar alarma en el repositorio
            val alarmID = alarmRepository.insertLocationAlarm(locationAlarm)
            // Configurar alarma en Android
            val insertedAlarm = alarmRepository.getAlarmByID(alarmID)
            insertedAlarm?.let { setAndroidAlarm(it) }
        }
    }

    /**
     * Activa o Desactiva la alarma de localización de ID pasado como parámetro,
     * según el flag indicado.
     * @param alarmID ID de la alarma a modificar.
     * @param enable flag para habilitar/deshabilitar la alarma de localización.
     */
    fun toggleAlarm(alarmID: Long, enable: Boolean){
        scope.launch {
            // Obtener la alarma de localización
            val alarm = alarmRepository.getAlarmByID(alarmID)
            alarm?.let {
                // Actualizar la alarma de localización
                alarm.updateHours()
                alarm.active = enable
                alarmRepository.updateLocationAlarm(alarm)
                // Activar o Desactivar alarma en Android.
                if(enable)
                    setAndroidAlarm(alarm) // Programar alarma
                else
                    cancelAndroidAlarm(alarm) // Cancelar alarma
            }
        }
    }

    /**
     * Cancela las alarmas de localización.
     */
    fun cancel(){
        val startServiceIntent = Intent(ctx, LocationForegroundService::class.java)
        startServiceIntent.action = Constants.ACTION_START_LOCATION_SERVICE
        alarmManager.cancel(getPendingIntentService(startServiceIntent, 999))

        val stopServiceIntent = Intent(ctx, LocationForegroundService::class.java)
        stopServiceIntent.action = Constants.ACTION_STOP_LOCATION_SERVICE
        alarmManager.cancel(getPendingIntentService(stopServiceIntent, 999))
    }

    /**
     * Método privado que devuelve el PendingIntent correspondiente
     * al Intent pasado como parámetro en función de la versión
     * del dispositivo.
     *
     * @param intent Intent con el servicio de localización.
     * @param id Id asociado al PendingIntent.
     *
     */
    private fun getPendingIntentService(intent: Intent, id: Int): PendingIntent {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            PendingIntent.getForegroundService(ctx, id, intent, 0)
        } else {
            PendingIntent.getService(ctx, id, intent, 0)
        }
    }

    /**
     * Se encarga de configurar una alarma de Android mediante
     * el AlarmManager con los datos de la alarma pasada como
     * parámetro.
     *
     * @param locationAlarm alarma de localización.
     */
    private fun setAndroidAlarm(locationAlarm: LocationAlarm) {
        val alarmID = locationAlarm.id ?: return // Si el ID es null -> salir de la función.
        // Alarma de inicio
        val startServiceIntent = Intent(ctx, LocationForegroundService::class.java)
        startServiceIntent.action = Constants.ACTION_START_LOCATION_SERVICE
        startServiceIntent.putExtra(Constants.EXTRA_LOCATION_ALARM_ID, alarmID)
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                locationAlarm.startDate.time,
                getPendingIntentService(startServiceIntent, alarmID.toInt())
        )
        // Alarma de fin
        val stopServiceIntent = Intent(ctx, LocationForegroundService::class.java)
        stopServiceIntent.action = Constants.ACTION_STOP_LOCATION_SERVICE
        stopServiceIntent.putExtra(Constants.EXTRA_LOCATION_ALARM_ID, alarmID)
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                locationAlarm.endDate.time,
                getPendingIntentService(stopServiceIntent, alarmID.toInt())
        )
    }

    /**
     * Utiliza el AlarmManager para cancelar la alarma pasada
     * como parámetro.
     *
     * @param locationAlarm Alarma de localización a cancelar.
     */
    private fun cancelAndroidAlarm(locationAlarm: LocationAlarm) {
        val alarmID = locationAlarm.id ?: return
        // Cancelar INICIO
        val startServiceIntent = Intent(ctx, LocationForegroundService::class.java)
        startServiceIntent.action = Constants.ACTION_START_LOCATION_SERVICE
        alarmManager.cancel(getPendingIntentService(startServiceIntent, alarmID.toInt()))

        // Cancelar FIN
        val stopServiceIntent = Intent(ctx, LocationForegroundService::class.java)
        stopServiceIntent.action = Constants.ACTION_STOP_LOCATION_SERVICE
        alarmManager.cancel(getPendingIntentService(stopServiceIntent, alarmID.toInt()))
    }
}