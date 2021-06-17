package es.uniovi.eii.contacttracker.fragments.settings

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.preference.PreferenceFragmentCompat
import es.uniovi.eii.contacttracker.R

/**
 * Fragmento para los Ajustes Generales de la Aplicación.
 */
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        // Esconder menú de la Toolbar
        val item1 = menu.findItem(R.id.appSettings)
        val item2 = menu.findItem(R.id.privacyPolicy)
        item1.isVisible = false
        item2.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }
}