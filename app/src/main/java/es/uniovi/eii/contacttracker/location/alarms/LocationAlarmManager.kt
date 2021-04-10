package es.uniovi.eii.contacttracker.location.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.LiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.location.receivers.LocationAlarmCommandBroadcastReceiver
import es.uniovi.eii.contacttracker.location.services.LocationForegroundService
import es.uniovi.eii.contacttracker.model.LocationAlarm
import es.uniovi.eii.contacttracker.repositories.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Clase que representa el Manager para las alarmas de localización.
 */
class LocationAlarmManager @Inject constructor(
        private val alarmManager: AlarmManager,
        private val alarmRepository: AlarmRepository,
        @ApplicationContext val ctx: Context
) {

    /**
     * Scope para la corrutina.
     */
    val scope = CoroutineScope(Job() + Dispatchers.IO)

    /**
     * Devuelve un LiveData con la lista de todas
     * las alarmas de localización.
     */
    fun getAllAlarms(): LiveData<List<LocationAlarm>> {
        return alarmRepository.getAllAlarms()
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
     * pasado como parámetro.
     *
     * @param alarmID ID de la alarma a eliminar.
     */
    fun deleteAlarm(alarmID: Long) {
        scope.launch {
            alarmRepository.deleteAlarmByID(alarmID)
        }
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
            alarmRepository.insertLocationAlarm(locationAlarm)
        }
    }

    /**
     * Establece una alarma de inicio y otra de fin para iniciar
     * el rastreo de ubicación y detenerlo a la hora de fin.
     *
     * @param locationAlarm datos de la alarma de localización.
     */
    fun set(locationAlarm: LocationAlarm){
        // Alarma de inicio
//        val startServiceIntent = Intent(ctx, LocationForegroundService::class.java)
//        startServiceIntent.action = LocationForegroundService.ACTION_START_LOCATION_SERVICE
//        startServiceIntent.putExtra(LocationForegroundService.EXTRA_COMMAND_FROM_ALARM, true)
//        alarmManager.setExactAndAllowWhileIdle(
//                AlarmManager.RTC_WAKEUP,
//                locationAlarmData.startDate.time,
//                getPendingIntentService(startServiceIntent, 999)
//        )
//        // Alarma de fin
//        val stopServiceIntent = Intent(ctx, LocationForegroundService::class.java)
//        stopServiceIntent.action = LocationForegroundService.ACTION_STOP_LOCATION_SERVICE
//        stopServiceIntent.putExtra(LocationForegroundService.EXTRA_COMMAND_FROM_ALARM, true)
//        alarmManager.setExactAndAllowWhileIdle(
//                AlarmManager.RTC_WAKEUP,
//                locationAlarmData.endDate.time,
//                getPendingIntentService(stopServiceIntent, 999)
//        )
        val startIntent = Intent(ctx, LocationAlarmCommandBroadcastReceiver::class.java)
        startIntent.action = LocationForegroundService.ACTION_START_LOCATION_SERVICE
        val startPI = PendingIntent.getBroadcast(ctx, 1999, startIntent, 0)
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                locationAlarm.startDate.time,
                startPI
        )

        val endIntent = Intent(ctx, LocationAlarmCommandBroadcastReceiver::class.java)
        endIntent.action = LocationForegroundService.ACTION_STOP_LOCATION_SERVICE
        val stopPI = PendingIntent.getBroadcast(ctx, 1999, endIntent, 0)

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                locationAlarm.endDate.time,
                stopPI
        )
    }

    /**
     * Cancela las alarmas de localización.
     */
    fun cancel(){
        val startServiceIntent = Intent(ctx, LocationForegroundService::class.java)
        startServiceIntent.action = LocationForegroundService.ACTION_START_LOCATION_SERVICE
        alarmManager.cancel(getPendingIntentService(startServiceIntent, 999))

        val stopServiceIntent = Intent(ctx, LocationForegroundService::class.java)
        stopServiceIntent.action = LocationForegroundService.ACTION_STOP_LOCATION_SERVICE
        alarmManager.cancel(getPendingIntentService(stopServiceIntent, 999))
    }


    /**
     * Método privado que devuelve el PendingIntent correspondiente
     * al Intent pasado como parámetro en función de la versión
     * del dispositivo.
     *
     * @param intent Intent con el servicio.
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



}