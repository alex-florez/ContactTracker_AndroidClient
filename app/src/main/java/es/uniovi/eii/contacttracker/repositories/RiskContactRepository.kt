package es.uniovi.eii.contacttracker.repositories

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.fragments.riskcontacts.CheckMode
import es.uniovi.eii.contacttracker.model.ResultWithRiskContacts
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.model.RiskContactWithLocation
import es.uniovi.eii.contacttracker.room.daos.RiskContactDao
import javax.inject.Inject
import java.util.Date

/**
 * Repositorio que gestiona todas las operaciones
 * relacionadas con los Contactos de Riesgo.
 */
class RiskContactRepository @Inject constructor(
        private val riskContactDao: RiskContactDao,
        @ApplicationContext private val ctx: Context
){

    /**
     * Referencia a las SharedPreferences.
     */
    private val sharedPrefs = ctx.getSharedPreferences(
        ctx.getString(R.string.shared_prefs_file_name), Context.MODE_PRIVATE)


    /**
     * Inserta en la base de datos el resultado de la comprobación
     * de contactos de riesgo.
     *
     * @param riskContactResult Resultado de la comprobación de contactos de riesgo.
     */
    suspend fun insert(riskContactResult: RiskContactResult) {
        // Crear objeto wrapper para cada Risk Contact
        val riskContacts = mutableListOf<RiskContactWithLocation>()
        for(rc in riskContactResult.riskContacts) {
            riskContacts.add(RiskContactWithLocation(rc, rc.contactLocations))
        }
        // Crear objeto Wrapper
        val wrapper = ResultWithRiskContacts(
                riskContactResult,
                riskContacts
        )
        // Insertar Resultado
        val resultId = riskContactDao.insert(wrapper.riskContactResult)
        // Insertar Contactos de Riesgo
        for(riskContactWithLocation in wrapper.riskContacts){
            riskContactWithLocation.riskContact.riskContactResultId = resultId // ID del resultado.
            val riskContactId = riskContactDao.insertRiskContact(riskContactWithLocation.riskContact)
            for(contactPoint in riskContactWithLocation.riskContactLocations){
                contactPoint.rcId = riskContactId // ID del Contacto de Riesgo.
            }
            // Insertar Puntos de Contacto
            riskContactDao.insertRiskContactLocations(riskContactWithLocation.riskContactLocations)
        }
    }

    /**
     * Devuelve los resultados de todas las comprobaciones realizadas.
     */
    suspend fun getAll(): List<RiskContactResult> {
        // Obtener lista de objetos wrapper
        val wrapperList = riskContactDao.getAllRiskContactResults()
        val result = mutableListOf<RiskContactResult>()
        wrapperList.forEach { wrapper ->
            val riskContactResult = RiskContactResult()
            riskContactResult.resultId = wrapper.riskContactResult.resultId
            riskContactResult.numberOfPositives = wrapper.riskContactResult.numberOfPositives
            riskContactResult.timestamp = wrapper.riskContactResult.timestamp
            // Transformar RiskContacts
            wrapper.riskContacts.forEach {
                val riskContact = RiskContact()
                riskContact.riskContactId = it.riskContact.riskContactId
                riskContact.riskContactResultId = it.riskContact.riskContactResultId
                riskContact.riskLevel = it.riskContact.riskLevel
                riskContact.riskPercent = it.riskContact.riskPercent
                riskContact.exposeTime = it.riskContact.exposeTime
                riskContact.meanProximity = it.riskContact.meanProximity
                riskContact.meanTimeInterval = it.riskContact.meanTimeInterval
                riskContact.startDate = it.riskContact.startDate
                riskContact.endDate = it.riskContact.endDate
                riskContact.contactLocations = it.riskContactLocations.toMutableList()
                riskContactResult.riskContacts.add(riskContact)
            }
            result.add(riskContactResult)
        }
        return result
    }

    /**
     * Establece el modo de comprobación.
     *
     * @param checkMode Nuevo modo de comprobación.
     */
    fun setCheckMode(checkMode: CheckMode){
        with(sharedPrefs.edit()){
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
        val stringMode = sharedPrefs.getString(ctx.getString(R.string.shared_prefs_risk_contact_check_mode), "MANUAL")
        return CheckMode.valueOf(stringMode!!)
    }

    /**
     * Establece la hora de la comprobación
     * almacenándola en las SharedPrefs.
     *
     * @param date Fecha de la comprobación.
     */
    fun setCheckHour(date: Date){
        with(sharedPrefs.edit()){
            putLong(ctx.getString(R.string.shared_prefs_risk_contact_check_hour), date.time)
            apply()
        }
    }

    /**
     * Devuelve la fecha con la hora de la comprobación
     * actualmente almacenada en las SharedPrefs.
     *
     * @return Fecha con la hora de la comprobación.
     */
    fun getCheckHour(): Date {
        val time = sharedPrefs.getLong(ctx.getString(R.string.shared_prefs_risk_contact_check_hour), Date().time)
        return Date(time)
    }
}