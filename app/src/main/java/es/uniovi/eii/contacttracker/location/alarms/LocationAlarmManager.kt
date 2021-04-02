package es.uniovi.eii.contacttracker.location.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.location.services.LocationForegroundService
import es.uniovi.eii.contacttracker.model.LocationAlarmData
import es.uniovi.eii.contacttracker.util.Utils
import java.util.*
import javax.inject.Inject

/**
 * Clase que representa el Manager para las alarmas de localización.
 */
class LocationAlarmManager @Inject constructor(
        private val alarmManager: AlarmManager,
        @ApplicationContext val ctx: Context
) {

    /**
     * Establece una alarma de inicio y otra de fin para iniciar
     * el rastreo de ubicación y detenerlo a la hora de fin.
     *
     * @param locationAlarmData datos de la alarma de localización.
     */
    fun set(locationAlarmData: LocationAlarmData){
        // Alarma de inicio
        val startServiceIntent = Intent(ctx, LocationForegroundService::class.java)
        startServiceIntent.action = LocationForegroundService.ACTION_START_LOCATION_SERVICE
        startServiceIntent.putExtra(LocationForegroundService.EXTRA_COMMAND_FROM_ALARM, true)
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                locationAlarmData.startDate.time,
                getPendingIntentService(startServiceIntent, 999)
        )
        // Alarma de fin
        val stopServiceIntent = Intent(ctx, LocationForegroundService::class.java)
        stopServiceIntent.action = LocationForegroundService.ACTION_STOP_LOCATION_SERVICE
        stopServiceIntent.putExtra(LocationForegroundService.EXTRA_COMMAND_FROM_ALARM, true)
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                locationAlarmData.endDate.time,
                getPendingIntentService(stopServiceIntent, 999)
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