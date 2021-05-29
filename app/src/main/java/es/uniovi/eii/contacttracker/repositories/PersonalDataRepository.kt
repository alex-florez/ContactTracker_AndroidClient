package es.uniovi.eii.contacttracker.repositories

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.model.PersonalData
import javax.inject.Inject

/**
 * Repositorio para los datos personales del usuario.
 */
class PersonalDataRepository @Inject constructor(
    @ApplicationContext private val ctx: Context
) {

    /**
     * Referencia a las sharedPreferences.
     */
    private val sharedPrefs = ctx.getSharedPreferences(
        ctx.getString(R.string.shared_prefs_file_name),
        Context.MODE_PRIVATE
    )

    /**
     * Almacena los datos personales.
     */
    fun save(personalData: PersonalData) {
        with(sharedPrefs.edit()) {
            putString(ctx.getString(R.string.shared_prefs_personal_data_dni), personalData.dni)
            putString(ctx.getString(R.string.shared_prefs_personal_data_name), personalData.name)
            putString(ctx.getString(R.string.shared_prefs_personal_data_surname), personalData.surname)
            putString(ctx.getString(R.string.shared_prefs_personal_data_phoneNumber), personalData.phoneNumber)
            putString(ctx.getString(R.string.shared_prefs_personal_data_city), personalData.city)
            putString(ctx.getString(R.string.shared_prefs_personal_data_cp), personalData.cp)
            apply()
        }
    }

    /**
     * Recupera los datos personales.
     */
    fun get(): PersonalData {
        sharedPrefs.apply {
            return PersonalData(
                getString(ctx.getString(R.string.shared_prefs_personal_data_dni), "")!!,
                getString(ctx.getString(R.string.shared_prefs_personal_data_name), "")!!,
                getString(ctx.getString(R.string.shared_prefs_personal_data_surname), "")!!,
                getString(ctx.getString(R.string.shared_prefs_personal_data_phoneNumber), "")!!,
                getString(ctx.getString(R.string.shared_prefs_personal_data_city), "")!!,
                getString(ctx.getString(R.string.shared_prefs_personal_data_cp), "")!!
            )
        }
    }
}