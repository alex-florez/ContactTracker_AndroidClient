package es.uniovi.eii.contacttracker.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.location.alarms.LocationAlarm
import es.uniovi.eii.contacttracker.location.services.LocationForegroundService
import es.uniovi.eii.contacttracker.riskcontact.alarms.RiskContactAlarm
import es.uniovi.eii.contacttracker.riskcontact.alarms.StartRiskContactCheckReceiver
import es.uniovi.eii.contacttracker.util.AndroidUtils
import javax.inject.Inject

/**
 * Clase de ayuda para gestionar alarmas de Android.
 */
class AlarmHelper @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val alarmManager: AlarmManager
) {

    /**
     * Establece la alarma de localización para ser ejecutada en
     * el framework de Android.
     *
     * @param locationAlarm Alarma de localización.
     * @return Devuelve true si la alarma se establece correctaente.
     */
    fun setLocationAlarm(locationAlarm: LocationAlarm): Boolean {
        if(locationAlarm.id == null)
            return false
        // Alarma de inicio
        val startServiceIntent = Intent(ctx, LocationForegroundService::class.java)
        startServiceIntent.action = Constants.ACTION_START_LOCATION_SERVICE
        startServiceIntent.putExtra(Constants.EXTRA_LOCATION_ALARM_ID, locationAlarm.id)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            locationAlarm.startDate.time,
            getPendingIntentForegroundService(startServiceIntent, locationAlarm.id.toInt())
        )
        // Alarma de fin
        val stopServiceIntent = Intent(ctx, LocationForegroundService::class.java)
        stopServiceIntent.action = Constants.ACTION_STOP_LOCATION_SERVICE
        stopServiceIntent.putExtra(Constants.EXTRA_LOCATION_ALARM_ID, locationAlarm.id)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            locationAlarm.endDate.time,
            getPendingIntentForegroundService(stopServiceIntent, locationAlarm.id.toInt())
        )
        return true
    }

    /**
     * Cancela la alarma de localización de ID pasado como parámetro en el
     * framework de Android.
     *
     * @param alarmID ID de la alarma de localización a cancelar.
     */
    fun cancelLocationAlarm(alarmID: Long)  {
        // Cancelar INICIO
        val startServiceIntent = Intent(ctx, LocationForegroundService::class.java)
        startServiceIntent.action = Constants.ACTION_START_LOCATION_SERVICE
        alarmManager.cancel(getPendingIntentForegroundService(startServiceIntent, alarmID.toInt()))

        // Cancelar FIN
        val stopServiceIntent = Intent(ctx, LocationForegroundService::class.java)
        stopServiceIntent.action = Constants.ACTION_STOP_LOCATION_SERVICE
        alarmManager.cancel(getPendingIntentForegroundService(stopServiceIntent, alarmID.toInt()))
    }

    /**
     * Establece una alarma de Android que dispara un Broadcast Receiver para
     * iniciar la comprobación de contactos de riesgo según la hora establecida
     * en la alarma pasada como parámetro.
     *
     * @param riskContactAlarm Alarma de comprobación de contactos de riesgo.
     * @return True si la alarma se establece correctamente, false en otro caso.
     */
    fun setRiskContactCheckAlarm(riskContactAlarm: RiskContactAlarm): Boolean {
        if(riskContactAlarm.id != null){
            val intent = Intent(ctx, StartRiskContactCheckReceiver::class.java)
            intent.putExtra(Constants.EXTRA_RISK_CONTACT_ALARM, AndroidUtils.toByteArray(riskContactAlarm))
            val pendingIntent = PendingIntent.getBroadcast(ctx, riskContactAlarm.id!!.toInt(), intent, 0)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, riskContactAlarm.startDate.time, pendingIntent)
            return true
        }
        return false
    }

    /**
     * Cancela la alarma de Android de comprobación de contactos de riesgo cuyo
     * ID coincide con el ID pasado como parámetro.
     *
     * @param id ID de la alarma de comprobación a cancelar.
     */
    fun cancelRiskContactCheckAlarm(id: Long) {
        val intent = Intent(ctx, StartRiskContactCheckReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(ctx, id.toInt(), intent, 0)
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Método privado que devuelve el PendingIntent para el foregroundservice
     * correspondiente al Intent pasado como parámetro en función de la versión
     * del dispositivo.
     *
     * @param intent Intent con el servicio de localización.
     * @param id Id asociado al PendingIntent.
     *
     */
    private fun getPendingIntentForegroundService(intent: Intent, id: Int): PendingIntent {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            PendingIntent.getForegroundService(ctx, id, intent, 0)
        } else {
            PendingIntent.getService(ctx, id, intent, 0)
        }
    }

}