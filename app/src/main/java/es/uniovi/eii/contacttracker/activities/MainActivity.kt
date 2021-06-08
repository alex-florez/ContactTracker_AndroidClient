package es.uniovi.eii.contacttracker.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.ActivityMainBinding
import es.uniovi.eii.contacttracker.fragments.DefaultBlankFragment
import es.uniovi.eii.contacttracker.fragments.history.HistoryPlaceholderFragment
import es.uniovi.eii.contacttracker.fragments.history.LocationHistoryFragment
import es.uniovi.eii.contacttracker.fragments.notifypositive.NotifyPositiveFragment
import es.uniovi.eii.contacttracker.fragments.riskcontacts.RiskContactFragment
import es.uniovi.eii.contacttracker.fragments.tracklocation.TrackerInfoFragment
import es.uniovi.eii.contacttracker.fragments.tracklocation.TrackerConfigurationFragment
import es.uniovi.eii.contacttracker.fragments.tracklocation.TrackLocationTabsFragment

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
            R.id.bottomMenuOption3 -> RiskContactFragment()
            R.id.bottomMenuOption4 -> HistoryPlaceholderFragment()
            else -> DefaultBlankFragment()
        }
    }

}