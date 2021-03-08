package es.uniovi.eii.contacttracker.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.ActivityMainBinding
import es.uniovi.eii.contacttracker.fragments.TrackLocationFragment
import es.uniovi.eii.contacttracker.fragments.SecondItemFragment
import es.uniovi.eii.contacttracker.fragments.ThirdItemFragment

class MainActivity : AppCompatActivity() {

    /**
     * View Binding
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * Map que contiene los fragments asociados a los items de menú,
     * y como claves los IDs de los items de menú.
     */
    private val fragmentsMap: MutableMap<Int, Fragment> = mutableMapOf()

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
        initFragmentsMap() // Crear fragments
        setListeners() // Listeners

        binding.bottomNavigationView.selectedItemId = R.id.bottomMenuOption1 // Establecer el fragment inicial como seleccionado
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
     * Instancia e inicializa los fragments principales asociados
     * al menú inferior y los almacena en el Map, junto con su clave
     * asociada.
     */
    private fun initFragmentsMap(){
        fragmentsMap[R.id.bottomMenuOption1] = TrackLocationFragment()
        fragmentsMap[R.id.bottomMenuOption2] = SecondItemFragment()
        fragmentsMap[R.id.bottomMenuOption3] = ThirdItemFragment()
    }

    /**
     * Reemplaza el fragment actual en el contenedor principal de Fragments
     * por el fragment asociado al id del item de menú pasado como parámetro.
     *
     * @param itemId id del item del menú inferior.
     */
    private fun setFragment(itemId: Int){
        supportFragmentManager.beginTransaction().apply {
            fragmentsMap[itemId]?.let{
                replace(R.id.main_fragment_container, it)
                commit()
            }
        }
    }

}