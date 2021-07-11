package es.uniovi.eii.contacttracker.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.fragments.riskcontacts.CheckMode
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.room.mappers.toResultWithRiskContacts
import es.uniovi.eii.contacttracker.room.mappers.toRiskContactResult
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