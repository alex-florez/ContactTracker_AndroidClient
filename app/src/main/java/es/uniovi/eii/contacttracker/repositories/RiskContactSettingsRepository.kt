package es.uniovi.eii.contacttracker.repositories

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.model.RiskContactConfig
import es.uniovi.eii.contacttracker.network.api.ConfigAPI
import es.uniovi.eii.contacttracker.network.apiCall
import es.uniovi.eii.contacttracker.network.model.ResultWrapper
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Repositorio para gestionar la configuración de la
 * Comprobación de Contactos de Riesgo.
 */
class RiskContactSettingsRepository @Inject constructor(
       private val configAPI: ConfigAPI,
       @ApplicationContext private val ctx: Context
){

    /**
     * Referencia a las Shared Preferences.
     */
    private val sharedPrefs = ctx.getSharedPreferences(
            ctx.getString(R.string.shared_prefs_file_name), Context.MODE_PRIVATE)

    /**
     * Recupera la configuración de la comprobación de contactos de riesgo
     * establecida actualmente a partir de las Shared Preferences en local y
     * de la configuración remota establecida en la nube por los administradores.
     *
     * @return Objeto con la configuración de la comprobación.
     */
    suspend fun retrieveRiskContactConfig(): RiskContactConfig {
        var config = RiskContactConfig()
        /* Recuperar configuración remota */
        when(val result = apiCall(Dispatchers.IO){ configAPI.getRiskContactConfig() }){
            is ResultWrapper.Success -> {
                config = result.value
            }
        }
        /* Recuperar configuración de las SharedPrefs */
        config.checkScope = sharedPrefs.getInt(
                ctx.getString(R.string.shared_prefs_risk_contact_check_scope), 3)
        return config
    }

}