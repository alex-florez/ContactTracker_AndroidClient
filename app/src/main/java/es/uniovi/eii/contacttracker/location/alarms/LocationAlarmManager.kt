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
import es.uniovi.eii.contacttracker.alarms.AlarmHelper
import es.uniovi.eii.contacttracker.location.services.LocationForegroundService
import es.uniovi.eii.contacttracker.model.Error
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.util.ValueWrapper
import javax.inject.Inject
import java.util.Date

/**
 * Manager para gestionar y programar las alarmas de localización.
 */
class LocationAlarmManager @Inject constructor(
        private val alarmHelper: AlarmHelper,
        private val locationRepository: LocationRepository,
        @ApplicationContext val ctx: Context
) {

    /**
     * Devuelve un LiveData con la lista de todas las alarmas de localización
     * establecidas actualmente.
     *
     * @return LiveData con el listado de alarmas de localización.
     */
    fun getAllAlarms(): LiveData<List<LocationAlarm>> {
        return locationRepository.getAllAlarms()
    }

    /**
     * Elimina la alarma de ID pasado como parámetro. Cancela dicha alarma
     * en el framework de Android y además la elimina de la base de datos.
     *
     * @param alarmID ID de la alarma de localización a eliminar.
     */
    suspend fun deleteAlarm(alarmID: Long) {
        // Cancelar alarma en el sistema Android.
        alarmHelper.cancelLocationAlarm(alarmID)
        // Eliminar alarma de la BDD
        locationRepository.deleteAlarmByID(alarmID)
    }

    /**
     * Comprueba si existen colisiones entre las alarmas de localización
     * almacenadas y la alarma pasada como parámetro. Las colisiones se producen
     * debido a la superposición total o parcial de las horas de inicio y de
     * fin de las alarmas.
     *
     * @param locationAlarm Alarma de localización.
     * @return Lista con las posibles alarmas con las que existe colisión.
     */
    private suspend fun checkAlarmCollisions(locationAlarm: LocationAlarm): List<LocationAlarm> {
        return locationRepository.getAlarmCollisions(locationAlarm)
    }

    /**
     * Se encarga de establecer una Alarma de Localización.
     * Se inserta en la base de datos y además se configura una
     * nueva alarma en Android.
     *
     * @param locationAlarm Alarma de localización.
     * @return Objeto ValueWrapper con Éxito o Fallo.
     */
    suspend fun setAlarm(locationAlarm: LocationAlarm): ValueWrapper<Unit> {
        locationAlarm.updateHours(Date()) // Actualizar horas de la alarma (si es necesario)
        if(locationAlarm.isValid()){ // Comprobar las horas de la alarma
            if(checkAlarmCollisions(locationAlarm).isEmpty()) { // Comprobar colisiones
                // Insertar alarma en el repositorio
                val alarmID = locationRepository.insertLocationAlarm(locationAlarm)
                // Configurar alarma en Android
                val insertedAlarm = locationRepository.getAlarmByID(alarmID)
                if(insertedAlarm != null)
                    alarmHelper.setLocationAlarm(insertedAlarm)
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
     *
     * @param alarmID ID de la alarma a modificar.
     * @param enable Flag para habilitar/deshabilitar la alarma de localización.
     */
    suspend fun toggleAlarm(alarmID: Long, enable: Boolean){
        // Obtener la alarma de localización
        val alarm = locationRepository.getAlarmByID(alarmID)
        alarm?.let {
            // Actualizar la alarma de localización en la base de datos
            alarm.updateHours(Date())
            alarm.active = enable
            locationRepository.updateLocationAlarm(alarm)
            // Activar o Desactivar alarma en Android.
            if(enable)
                alarmHelper.setLocationAlarm(alarm) // Programar alarma
            else
                alarmHelper.cancelLocationAlarm(alarmID)// Cancelar alarma
        }
    }
}