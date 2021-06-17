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
        requireActivity().actionBar?.setDisplayHomeAsUpEnabled(true)
    }
}