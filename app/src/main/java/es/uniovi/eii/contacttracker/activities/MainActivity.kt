package es.uniovi.eii.contacttracker.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.ActivityMainBinding
import es.uniovi.eii.contacttracker.fragments.DefaultBlankFragment
import es.uniovi.eii.contacttracker.fragments.history.HistoryPlaceholderFragment
import es.uniovi.eii.contacttracker.fragments.history.LocationHistoryFragment
import es.uniovi.eii.contacttracker.fragments.notifypositive.NotifyPositiveFragment
import es.uniovi.eii.contacttracker.fragments.riskcontacts.ResultDetailsFragment
import es.uniovi.eii.contacttracker.fragments.riskcontacts.RiskContactFragment
import es.uniovi.eii.contacttracker.fragments.riskcontacts.RiskContactPlaceholder
import es.uniovi.eii.contacttracker.fragments.riskcontacts.RiskContactTabsFragment
import es.uniovi.eii.contacttracker.fragments.tracklocation.TrackerInfoFragment
import es.uniovi.eii.contacttracker.fragments.tracklocation.TrackerConfigurationFragment
import es.uniovi.eii.contacttracker.fragments.tracklocation.TrackLocationTabsFragment
import es.uniovi.eii.contacttracker.model.RiskContactResult

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    /**
     * View Binding
     */
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUp()
        processIntent(intent)
    }

    /**
     * Callback de llamada cuando la Aplicación recibe un nuevo Intent.
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let{
            processIntent(it)
        }
    }

    /**
     * Se encarga de procesar el intent recibido por la Activity.
     */
    private fun processIntent(intent: Intent) {
        val action = intent.action
        val extras = intent.extras
        if(action != null && extras != null){
            when(action){
                // Mostrar detalles del resultado de una comprobación
                Constants.ACTION_SHOW_RISK_CONTACT_RESULT -> {
                    showRiskContactResult(extras)
                }
            }
        }
    }

    /**
     * Muestra los resultados del contacto de riesgo recibido en el Intent,
     * mediante un nuevo fragment.
     */
    private fun showRiskContactResult(extras: Bundle) {
        val riskContactResult = extras.getParcelable<RiskContactResult>(Constants.EXTRA_RISK_CONTACT_RESULT)
        if(riskContactResult != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, ResultDetailsFragment.newInstance(riskContactResult))
                .commit()
        }
    }

    /**
     * Se encarga de realizar las operaciones iniciales
     * una vez creada la Activity.
     */
    private fun setUp(){
        setListeners() // Listeners

        // Establecer el fragment inicial como seleccionado
        binding.bottomNavigationView.selectedItemId = R.id.bottomMenuOption1
    }

    /**
     * Método encargado de establecer y registrar los listeners
     * correspondientes a los componentes de la vista.
     */
    private fun setListeners(){
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            setFragment(it.itemId)
            true
        }
    }

    /**
     * Reemplaza el fragment actual en el contenedor principal de Fragments
     * por el fragment asociado al id del item de menú pasado como parámetro.
     *
     * @param itemId id del item del menú inferior.
     */
    private fun setFragment(itemId: Int){
        supportFragmentManager.beginTransaction().apply {
           getMenuFragment(itemId).let{
                replace(R.id.main_fragment_container, it)
                commit()
            }
        }
    }

    /**
     * Recibe como parámetro un id que se corresponde con los
     * ID's de los items de menú, y devuelve una nueva instancia
     * del Fragment asociado a dicha opción de menú.
     */
    private fun getMenuFragment(id: Int): Fragment {
        return when(id){
            R.id.bottomMenuOption1 -> TrackLocationTabsFragment()
            R.id.bottomMenuOption2 -> NotifyPositiveFragment()
            R.id.bottomMenuOption3 -> RiskContactPlaceholder()
            R.id.bottomMenuOption4 -> HistoryPlaceholderFragment()
            else -> DefaultBlankFragment()
        }
    }

}