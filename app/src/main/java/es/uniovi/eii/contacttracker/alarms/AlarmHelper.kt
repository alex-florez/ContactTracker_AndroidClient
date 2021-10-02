package es.uniovi.eii.contacttracker.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.Constants
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

}