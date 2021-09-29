package es.uniovi.eii.contacttracker.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.fragments.riskcontacts.CheckMode
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.riskcontact.alarms.RiskContactAlarm
import es.uniovi.eii.contacttracker.room.daos.RiskContactAlarmDao
import es.uniovi.eii.contacttracker.room.mappers.toResultWithRiskContacts
import es.uniovi.eii.contacttracker.room.mappers.toRiskContactResult
import es.uniovi.eii.contacttracker.room.daos.RiskContactDao
import es.uniovi.eii.contacttracker.util.DateUtils
import javax.inject.Inject
import java.util.Date

/**
 * Repositorio que gestiona todas las operaciones relacionadas con
 * los Contactos de Riesgo, incluidas las alarmas de comprobación de contactos.
 */
class RiskContactRepository @Inject constructor(
        private val riskContactDao: RiskContactDao,
        private val riskContactAlarmDao: RiskContactAlarmDao,
        private val sharedPreferences: SharedPreferences,
        @ApplicationContext private val ctx: Context
){

    /**
     * Inserta en la base de datos el resultado de la comprobación
     * de contactos de riesgo. Devuelve el ID del resultado recién insertado.
     *
     * @param riskContactResult Resultado de la comprobación de contactos de riesgo.
     * @return ID del Resultado de la comprobación.
     */
    suspend fun insert(riskContactResult: RiskContactResult): Long {
        val wrapper = toResultWithRiskContacts(riskContactResult)
        // Insertar Resultado
        val resultId = riskContactDao.insert(wrapper.riskContactResult)
        // Insertar Contactos de Riesgo
        wrapper.riskContacts.forEach {
            it.riskContact.riskContactResultId = resultId // ID del resultado
            val riskContactId = riskContactDao.insertRiskContact(it.riskContact) // Insertar contacto de riesgo.
            // Insertar localizaciones de contacto
            it.riskContactLocations.forEach { riskContactLocation ->
                riskContactLocation.riskContactId = riskContactId // ID del contacto de riesgo
            }
            riskContactDao.insertRiskContactLocations(it.riskContactLocations)
        }
        return resultId
    }

    /**
     * Devuelve los resultados de todas las comprobaciones realizadas.
     * Dado que se trata de varias relaciones 1 a n, se realiza una transformación
     * de los objetos wrapper a los objetos originales del dominio. Se utiliza una
     * transformación para mantener el comportamiento del LiveData.
     *
     * @return LiveData con la lista de resultados convertidos a objetos del dominio.
     */
    fun getAll(): LiveData<List<RiskContactResult>> {
        val wrapperList = riskContactDao.getAllRiskContactResults()
        // Transformar livedata para convertir los objetos wrapper a objetos del dominio
        return Transformations.map(wrapperList) {
            val mappedResults = mutableListOf<RiskContactResult>()
            it.forEach { result ->
                mappedResults.add(toRiskContactResult(result))
            }
            mappedResults.toList()
        }
    }

    /**
     * Establece el modo de comprobación.
     *
     * @param checkMode Nuevo modo de comprobación.
     */
    fun setCheckMode(checkMode: CheckMode){
        with(sharedPreferences.edit()){
            putString(ctx.getString(R.string.shared_prefs_risk_contact_check_mode), checkMode.name)
            apply()
        }
    }

    /**
     * Devuelve el modo de comprobación seleccionado
     * actualmente.
     *
     * @return Modo de comprobación seleccionado.
     */
    fun getCheckMode(): CheckMode {
        val stringMode = sharedPreferences.getString(ctx.getString(R.string.shared_prefs_risk_contact_check_mode), "MANUAL")
        return CheckMode.valueOf(stringMode!!)
    }

    /**
     * Inserta la alarma de comprobación de contactos en la base de datos.
     *
     * @param alarm Alarma de comprobación de contactos.
     * @return ID de la alarma recién insertada.
     */
    suspend fun insertAlarm(alarm: RiskContactAlarm): Long {
        return riskContactAlarmDao.insert(alarm)
    }

    /**
     * Elimina la alarma de comprobación de ID pasado como parámetro de la
     * base de datos.
     *
     * @param alarmID ID de la alarma de comprobación.
     */
    suspend fun removeAlarm(alarmID: Long) {
        riskContactAlarmDao.remove(alarmID)
    }

    /**
     * Actualiza las alarmas de comprobación con los nuevos datos incluidos
     * en la lista de alarmas de comprobación pasada como parámetro.
     *
     * @param riskContactAlarms Lista con las nuevas alarmas de comprobación.
     */
    suspend fun updateAlarms(riskContactAlarms: List<RiskContactAlarm>) {
        riskContactAlarmDao.update(riskContactAlarms)
    }

    /**
     * Devuelve todas las alarmas de comprobación almacenadas actualmente
     * en la base de datos.
     *
     * @return Lista con todas las alarmas de comprobación.
     */
    suspend fun getAlarms(): List<RiskContactAlarm> {
        return riskContactAlarmDao.getAll()
    }

    /**
     * Devuelve la alarma de comprobación de id pasado como parámetro.
     *
     * @param id Id de la alarma de comprobación.
     * @return Alarma de comprobación o Null si no existe.
     */
    suspend fun getAlarmById(id: Long): RiskContactAlarm? {
        return riskContactAlarmDao.getById(id)
    }

    /**
     * Devuelve una lista con todas las alarmas de comprobación establecidas en la hora
     * que coincide con la hora de la fecha pasada como parámetro.
     *
     * @param date Fecha por la que filtrar.
     * @return Lista con las alarmas de comprobación cuya hora coincide con la indicada.
     */
    suspend fun getAlarmsBySetHour(date: Date): List<RiskContactAlarm> {
        val dateString = DateUtils.formatDate(date, "HH:mm:ss")
        return riskContactAlarmDao.getAlarmsBySetHour(dateString)
    }
}