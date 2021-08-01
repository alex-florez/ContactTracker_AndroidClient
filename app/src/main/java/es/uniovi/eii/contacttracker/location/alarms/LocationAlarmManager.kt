package es.uniovi.eii.contacttracker.location.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.LiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.location.services.LocationForegroundService
import es.uniovi.eii.contacttracker.model.Error
import es.uniovi.eii.contacttracker.repositories.AlarmRepository
import es.uniovi.eii.contacttracker.util.ValueWrapper
import javax.inject.Inject

/**
 * Manager para gestionar y programar las alarmas de localización.
 */
class LocationAlarmManager @Inject constructor(
        private val alarmManager: AlarmManager,
        private val alarmRepository: AlarmRepository,
        @ApplicationContext val ctx: Context
) {

    /**
     * Devuelve un LiveData con la lista de todas
     * las alarmas de localización.
     */
    fun getAllAlarms(): LiveData<List<LocationAlarm>> {
        return alarmRepository.getAllAlarms()
    }

    /**
     * Elimina la alarma de ID pasado como parámetro. Cancela dicha alarma
     * en el framework de Android y además la elimina de la base de datos.
     *
     * @param alarmID ID de la alarma de localización a eliminar.
     */
    suspend fun deleteAlarm(alarmID: Long) {
        // Cancelar alarma en el sistema Android.
        cancelAndroidAlarm(alarmID)
        // Eliminar alarma de la BDD
        alarmRepository.deleteAlarmByID(alarmID)
    }

    /**
     * Comprueba si existen colisiones entre las alarmas de localización
     * almacenadas y la alarma pasada como parámetro.
     *
     * @param locationAlarm alarma de localización.
     * @return lista con las alarmas con las que existe colisión.
     */
    private suspend fun checkAlarmCollisions(locationAlarm: LocationAlarm): List<LocationAlarm> {
        return alarmRepository.getAlarmCollisions(locationAlarm)
    }

    /**
     * Se encarga de establecer una Alarma de Localización.
     * Se inserta en la base de datos y además se configura una
     * nueva alarma en Android.
     *
     * @param locationAlarm alarma de localización.
     * @return Objeto ValueWrapper con Éxito o Fallo.
     */
    suspend fun setAlarm(locationAlarm: LocationAlarm): ValueWrapper<Unit> {
        locationAlarm.updateHours() // Actualizar horas de la alarma (si es necesario)
        if(locationAlarm.isValid()){ // Comprobar las horas de la alarma
            if(checkAlarmCollisions(locationAlarm).isEmpty()) { // Comprobar colisiones
                // Insertar alarma en el repositorio
                val alarmID = alarmRepository.insertLocationAlarm(locationAlarm)
                // Configurar alarma en Android
                val insertedAlarm = alarmRepository.getAlarmByID(alarmID)
                insertedAlarm?.let { setAndroidAlarm(it) }
                return ValueWrapper.Success(Unit)
            }
            return ValueWrapper.Fail(Error.ALARM_COLLISION)
        } else {
            return ValueWrapper.Fail(Error.INVALID_ALARM)
        }
    }

    /**
     * Activa o Desactiva la alarma de localización de ID pasado como parámetro,
     * según el flag indicado.
     * @param alarmID ID de la alarma a modificar.
     * @param enable flag para habilitar/deshabilitar la alarma de localización.
     */
    suspend fun toggleAlarm(alarmID: Long, enable: Boolean){
        // Obtener la alarma de localización
        val alarm = alarmRepository.getAlarmByID(alarmID)
        alarm?.let {
            // Actualizar la alarma de localización en la base de datos
            alarm.updateHours()
            alarm.active = enable
            alarmRepository.updateLocationAlarm(alarm)
            // Activar o Desactivar alarma en Android.
            if(enable)
                setAndroidAlarm(alarm) // Programar alarma
            else
                cancelAndroidAlarm(alarmID) // Cancelar alarma
        }
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
     * @param alarmID ID de la alarma de localización a cancelar.
     */
    private fun cancelAndroidAlarm(alarmID: Long) {
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