package es.uniovi.eii.contacttracker.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.ActivityMainBinding
import es.uniovi.eii.contacttracker.fragments.DefaultBlankFragment
import es.uniovi.eii.contacttracker.fragments.history.LocationHistoryFragment
import es.uniovi.eii.contacttracker.fragments.notifypositive.NotifyPositiveFragment
import es.uniovi.eii.contacttracker.fragments.riskcontacts.ResultDetailsFragment
import es.uniovi.eii.contacttracker.fragments.riskcontacts.RiskContactFragment
import es.uniovi.eii.contacttracker.fragments.riskcontacts.RiskContactTabsFragment
import es.uniovi.eii.contacttracker.fragments.settings.SettingsFragment
import es.uniovi.eii.contacttracker.fragments.tracklocation.TrackLocationTabsFragment
import es.uniovi.eii.contacttracker.model.RiskContactResult

/**
 * Actividad principal de la aplicación.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    /**
     * View Binding
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * Nav Controller: Controla y gestiona la navegación a través
     * del Grafo de Navegación general de la app.
     */
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* Re-establecer el theme principal (quitar SplashScreen) */
        setTheme(R.style.Theme_ContactTracker)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavController() // Referencia al NavController
        setUpAppBar() // Configurar App Bar
        setUpBottomNavigation() // Configurar Bottom Navigation

        processIntent(intent)
    }

    /* Redefine el comportamiento al pulsar sobre la flecha de atrás. */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
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
     * Creación del menú de opciones de la Toolbar.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    /**
     * Callback para los eventos de Click sobre las opciones del menú de la Toolbar.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Derivar en el NavController la navegación de las opciones de menú
        return item.onNavDestinationSelected(navController)
                || super.onOptionsItemSelected(item)
    }

    /**
     * Muestra el fragmento de configuración general de la
     * aplicación.
     */
    private fun goToSettings(){
//        supportFragmentManager.beginTransaction()
//            .setCustomAnimations(R.anim.slide_in_bottom, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.slide_out_top)
//            .replace(R.id.main_fragment_container, SettingsFragment())
//            .addToBackStack("Settings")
//            .commit()
    }

    /**
     * Inicializa y recupera una referencia al Nav Controller.
     */
    private fun initNavController() {
        // NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    /**
     * Inicializa y configura la AppBar junto con el Nav Controller.
     */
    private fun setUpAppBar() {
        // Varios destinos de alto nivel (sin Back Button)
        val appBarConfig = AppBarConfiguration(setOf(
            R.id.tracker,
            R.id.notifyPositive,
            R.id.riskContacts,
            R.id.locationHistory))
        // Vincular NavController con la ActionBar
        setupActionBarWithNavController(navController, appBarConfig)
    }

    /**
     * Inicializa y configura la navegación principal de la aplicación.
     * Vincula el Bottom Navigation View junto con las opciones de menú y
     * el Navigation Controller que gestiona el grafo de navegación.
     */
    private fun setUpBottomNavigation() {
        // Vincular BottomNavigationView + NavController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
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
//        if(riskContactResult != null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.main_fragment_container, ResultDetailsFragment.newInstance(riskContactResult))
//                .commit()
//        }
    }

}