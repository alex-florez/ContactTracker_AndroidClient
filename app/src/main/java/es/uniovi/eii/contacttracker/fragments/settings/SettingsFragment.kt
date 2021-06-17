package es.uniovi.eii.contacttracker.fragments.settings

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.core.view.iterator
import androidx.preference.PreferenceFragmentCompat
import es.uniovi.eii.contacttracker.R

/**
 * Fragmento para los Ajustes Generales de la Aplicación.
 */
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Establecer la SharedPreferences Custom
        preferenceManager.sharedPreferencesName = getString(R.string.shared_prefs_file_name)
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Permite a este fragmento manejar el menú de opciones.
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        // Esconder menú de la Toolbar
        for(item in menu.iterator()){
            item.isVisible = false
        }
        super.onPrepareOptionsMenu(menu)
    }
}