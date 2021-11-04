package es.uniovi.eii.contacttracker.fragments.settings

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ListView
import androidx.core.view.iterator
import androidx.core.view.marginLeft
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.notifications.InAppNotificationManager
import javax.inject.Inject

/**
 * Fragmento para los Ajustes Generales de la Aplicación.
 */
@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    /* Manager de Notificaciones Internas */
    @Inject lateinit var inAppNotificationManager: InAppNotificationManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Establecer la SharedPreferences Custom
        preferenceManager.sharedPreferencesName = getString(R.string.shared_prefs_file_name)
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        setChangeListeners()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Permite a este fragmento manejar el menú de opciones.
    }

    /**
     * Establece los listeners de cambio de valor para aquellas shared
     * preferences que lo requieran.
     */
    private fun setChangeListeners() {
        /* Habilitar/Deshabilitar las notificaciones de positivos */
        findPreference<CheckBoxPreference>(getString(R.string.shared_prefs_positives_notifications))
            ?.setOnPreferenceChangeListener { _, newValue ->
                inAppNotificationManager.togglePositivesNotifications(newValue as Boolean)
//                if(enable) {
//                    // Subscribirse al Topic de FCM 'positives'
//                    Firebase.messaging.subscribeToTopic("positives")
//                        .addOnCompleteListener {
//                            var msg = "Cliente Android subscrito al topic 'positives'"
//                            if(!it.isSuccessful){
//                                msg = "Error al subscribirse al Topic 'positives' de FCM"
//                            }
//                            Log.d(TAG, msg)
//                        }
//                } else {
//                    // Desubscribirse del Topic de FCM 'positives'
//                    Firebase.messaging.unsubscribeFromTopic("positives")
//                        .addOnCompleteListener {
//                            var msg = "Cliente Android desubscrito del topic 'positives'"
//                            if(!it.isSuccessful){
//                                msg = "Error al desubscribirse del Topic 'positives' de FCM"
//                            }
//                            Log.d(TAG, msg)
//                        }
//                }
                true
            }
    }
}