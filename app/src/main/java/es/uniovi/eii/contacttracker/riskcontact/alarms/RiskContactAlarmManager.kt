package es.uniovi.eii.contacttracker.riskcontact.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.model.Error
import es.uniovi.eii.contacttracker.repositories.RiskContactRepository
import es.uniovi.eii.contacttracker.util.ValueWrapper
import javax.inject.Inject


/* Constantes */

/* N.º máximo de alarmas de comprobación que pueden ser establecidas. */
const val MAX_ALARM_COUNT = 3

/**
 * Clase encargada de gestionar la programación periódica de alarmas
 * para la comprobación de contactos de riesgo.
 */
class RiskContactAlarmManager @Inject constructor(
    private val alarmManager: AlarmManager,
    private val riskContactRepository: RiskContactRepository,
    @ApplicationContext private val ctx: Context
){

    /**
     * Establece una nueva alarma de comprobación de contactos. Inserta
     * la alarma en la base de datos y crea una nueva alarma de Android
     * mediante el AlarmManager. Devuelve un Wrapper con la nueva alarma
     * insertada o con un error determinado.
     *
     * @param riskContactAlarm Alarma de comprobación de contactos.
     * @return ValueWrapper con la nueva alarma de comprobación o un error determinado.
     */
    suspend fun set(riskContactAlarm: RiskContactAlarm): ValueWrapper<RiskContactAlarm> {
        // Actualizar las horas de la alarma si es necesario y eliminar segundos
        riskContactAlarm.updateHours()
        if(checkAlarmsCollision(riskContactAlarm)) { // Comprobar si existen colisiones entre alarmas.
            return if(checkAlarmCount()) { // Comprobar si se ha superado el límite de alarmas.
                // Insertar alarma en la base de datos
                val alarmID = riskContactRepository.insertAlarm(riskContactAlarm)
                riskContactAlarm.id = alarmID
                // Crear PendingIntent
                val i = Intent(ctx, StartRiskContactCheckReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(ctx, alarmID.toInt(), i, 0)
                // Configurar alarma de Android
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, riskContactAlarm.startDate.time, pendingIntent)
                ValueWrapper.Success(riskContactAlarm)
            } else {
                ValueWrapper.Fail(Error.RISK_CONTACT_ALARM_COUNT_LIMIT_EXCEEDED)
            }
        } else {
            return ValueWrapper.Fail(Error.RISK_CONTACT_ALARM_COLLISION)
        }
    }

    /**
     * Elimina la alarma de ID pasado como parámetro. Se elimina de la base
     * de datos y también del framework de Android para que no sea disparada.
     *
     * @param alarmID ID de la alarma a eliminar.
     */
    suspend fun remove(alarmID: Long) {
        // Eliminar alarma de la Base de Datos
        riskContactRepository.removeAlarm(alarmID)

        // Eliminar alarma del AlarmManager de Android
        val i = Intent(ctx, StartRiskContactCheckReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(ctx, alarmID.toInt(), i, 0)
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Activa o desactiva las alarmas de comprobación establecidas actualmente.
     * No las elimina de la base de datos, sino que modifica el PendingIntent en
     * el AlarmManager.
     *
     * @param activate Flag para activar/desactivar las alarmas.
     */
    suspend fun toggle(activate: Boolean) {
        // Recuperar todas las alarmas de comprobación
        val alarms = riskContactRepository.getAlarms()

        // Actualizar alarmas en la base de datos
        alarms.forEach { alarm ->
            alarm.active = activate
            alarm.updateHours() // Actualizar horas si es necesario.
        }
        riskContactRepository.updateAlarms(alarms)
        // Activar/Desactivar las alarmas de Android
        alarms.forEach { alarm ->
            alarm.id?.let { alarmID ->
                val i = Intent(ctx, StartRiskContactCheckReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(ctx, alarmID.toInt(), i, 0)
                if(activate) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.startDate.time, pendingIntent)
                } else {
                    alarmManager.cancel(pendingIntent)
                }
            }
        }
    }

    /**
     * Devuelve una lista con todas las alarmas de comprobación
     * establecidas actualmente.
     *
     * @return Lista con las alarmas de comprobación.
     */
    suspend fun getAllAlarms(): List<RiskContactAlarm> {
        return riskContactRepository.getAlarms()
    }

    /**
     * Comprueba que si existen colisiones entre las alarmas de comprobación,
     * es decir, si hay varias alarmas establecidas a la misma hora.
     *
     * @param alarm Alarma de comprobación a checkear.
     * @return Devuelve true si existen colisiones.
     */
    private suspend fun checkAlarmsCollision(alarm: RiskContactAlarm): Boolean {
        return riskContactRepository.getAlarmsBySetHour(alarm.startDate).isEmpty()
    }

    /**
     * Comprueba si se supera el límite de alarmas que se permiten establecer.
     */
    private suspend fun checkAlarmCount(): Boolean {
        return riskContactRepository.getAlarms().size < MAX_ALARM_COUNT
    }

}