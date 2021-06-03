package es.uniovi.eii.contacttracker.fragments.tracklocation

import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.adapters.locations.UserLocationAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentTrackerBinding
import es.uniovi.eii.contacttracker.fragments.dialogs.timepicker.OnTimeSetListener
import es.uniovi.eii.contacttracker.fragments.dialogs.timepicker.TimePickerFragment
import es.uniovi.eii.contacttracker.location.services.LocationForegroundService
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.util.PermissionUtils
import es.uniovi.eii.contacttracker.util.Utils
import es.uniovi.eii.contacttracker.viewmodels.TrackerViewModel
import java.util.*


/**
 * Fragment que representa la opción del RASTREADOR DE UBICACIÓN.
 *
 * Contiene la funcionalidad para iniciar y pausar el rastreo de ubicación
 * de manera manual, así como visualizar información del rastreo de ubicación
 * en curso.
 */
@AndroidEntryPoint
class TrackerFragment : Fragment() {

    /**
     * View Binding
     */
    private lateinit var binding: FragmentTrackerBinding

    /**
     * Adapter para las localizaciones de Usuario.
     */
    private lateinit var userLocationAdapter: UserLocationAdapter

    /**
     * ViewModel
     */
    private val viewModel:TrackerViewModel by viewModels()

    /**
     * Broadcast Receiver para las alarmas de inicio y de fin.
     */
    private var locationAlarmBroadCastReceiver: LocationAlarmBroadCastReceiver? = null

    /**
     * BroadCast Receiver para actualizar el Adapter
     * con las actualizaciones de localización.
     */
    private var locationReceiver: LocationUpdateBroadcastReceiver? = null

    // BROADCAST RECEIVERS
    /**
     * Clase interna privada que representa el BroadCast receiver que
     * es disparado cada vez que se inicia o detiene un servicio de localización
     * desde una alarma.
     */
    inner class LocationAlarmBroadCastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("RECEIVER", "Alarma de localización recibida.")
            when(intent?.getStringExtra(EXTRA_LOCATION_ALARM_COMMAND)) {
                Constants.ACTION_START_LOCATION_SERVICE -> {
                    // Notificar LiveData servicio ACTIVO
                    viewModel.setIsLocationServiceActive(true)
                }
                Constants.ACTION_STOP_LOCATION_SERVICE -> {
                    // Notificar LiveData servicio INACTIVO
                    viewModel.setIsLocationServiceActive(false)
                }
            }
        }

    }

    /**
     * Broadcast Receiver que se dispara cuando se recibe
     * una nueva localización, para actualizar la UI.
     */
    inner class LocationUpdateBroadcastReceiver() : BroadcastReceiver() {

        private val TAG = "LocationUpdateBroadcastReceiver"

        override fun onReceive(context: Context?, intent: Intent?) {
            val location: UserLocation? = intent?.getParcelableExtra(Constants.EXTRA_LOCATION)
            location?.let {
                Log.d(TAG, LocationUtils.format(it))
                userLocationAdapter.addUserLocation(location)
                viewModel.setAreLocationsAvailable(userLocationAdapter.areLocationsAvailable())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createLocationsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackerBinding.inflate(inflater, container, false)
        setListeners()
        setObservers()
        initLocationsRecyclerView()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Establecer estado de los Switches
        initLocationTrackSwitch()
        registerReceivers()
        viewModel.setAreLocationsAvailable(userLocationAdapter.areLocationsAvailable())
    }

    override fun onPause() {
        super.onPause()
        unregisterReceivers()
    }

    /**
     * Resultado de la solicitud de permisos de localización.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_ID -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startLocationService()
                }
            }
        }
    }

    /**
     * Método encargado de solicitar los permisos necesarios
     * para la localización.
     */
    private fun requestLocationPermissions(){
        val permissions = arrayListOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        // Si la versión es Android Q (API 29) soliticar también el permiso de Localización en Background
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            permissions.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        requestPermissions(permissions.toTypedArray(), LOCATION_PERMISSION_REQUEST_ID)
    }

    /**
     * Establece los listeners para cada componente de la UI.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners(){
        // Switch para Activar/Desactivar el servicio de localización.
        binding.layoutCardLocationTracker.switchTrackLocation.setOnCheckedChangeListener{_, isChecked ->
            toggleLocationService(isChecked)
        }
    }

    /**
     * Método encargado de configurar los observers
     * para observar los datos del ViewModel.
     */
    private fun setObservers(){
        viewModel.apply {
            // Flag de servicio de localización activo
            isLocationServiceActive.observe(viewLifecycleOwner) {isActive ->
                binding.layoutCardLocationTracker.switchTrackLocation.isChecked = isActive
            }
            // Flag para la lista de localizaciones vacía o no
            areLocationsAvailable.observe(viewLifecycleOwner) { available ->
                binding.labelNoLocations.visibility = if (available) View.GONE else View.VISIBLE
            }
        }
    }

    /**
     * Método invocado al pulsar sobre el Switch para activar o
     * desactivar el servicio de localización.
     */
    private fun toggleLocationService(toggle: Boolean) {
        if(toggle)
            startLocationService()
        else
            stopLocationService()
    }


    /**
     * Se encarga de inicializar el servicio de localización
     * en 1er plano para obtener actualizaciones de localización.
     */
    private fun startLocationService(){
        doLocationChecks ({ // Éxito
            if(!LocationUtils.isLocationServiceRunning(requireContext())) { // Comprobar que el servicio no esté ya ejecutándose
                sendCommandToLocationService(Constants.ACTION_START_LOCATION_SERVICE)
                viewModel.setIsLocationServiceActive(true)
            } else {
                Log.d(TAG, "Ya se está ejecutando un servicio de localización")
            }
        }, { // Fracaso
            binding.layoutCardLocationTracker.switchTrackLocation.isChecked = false
        })
    }

    /**
     * Método encargado de detener el rastreador de ubicación.
     */
    private fun stopLocationService(){
        if(LocationUtils.isLocationServiceRunning(requireContext())) { // Comprobar que el servicio se esté ejecutando.
            sendCommandToLocationService(Constants.ACTION_STOP_LOCATION_SERVICE)
            Snackbar.make(binding.root, R.string.labelStoppedService, Snackbar.LENGTH_LONG).let{ // Mostrar Snackbar
                it.anchorView = requireActivity().findViewById(R.id.bottomNavigationView) // Mostrarlo encima del BottomNavView
                it.show()
            }
            userLocationAdapter.clearLocations() // Vaciar Adapter de localizaciones
            viewModel.setIsLocationServiceActive(false) // Notificar al LiveData de servicio activo
            viewModel.setAreLocationsAvailable(userLocationAdapter.areLocationsAvailable()) // Notificar al LiveData de disponibilidad de localizaciones
        }
    }

    /**
     * Método encargado de iniciar o detener el servicio de localización, que será
     * ejecutado en segundo plano y hará uso del Tracker de ubicación.
     * Recibe como parámetro la acción a realizar: START / STOP
     *
     * @param acción a realizar por el servicio (START/STOP)
     */
    private fun sendCommandToLocationService(action: String){
        Intent(requireContext(), LocationForegroundService::class.java).let {
            it.action = action
            it.putExtra(Constants.EXTRA_COMMAND_FROM_ALARM, false)
            ContextCompat.startForegroundService(requireContext(), it)
        }
    }

    /**
     * Inicializa el Switch de activación del servicio
     * de rastreo de ubicación, según se esté ejecutando o no
     * el servicio de localización.
     */
    private fun initLocationTrackSwitch(){
        viewModel.setIsLocationServiceActive(LocationUtils.isLocationServiceRunning(requireContext()))
    }

    /**
     * Método privado que realiza las comprobaciones de PERMISOS y de
     * configuración relativa a la localización. Si las comprobaciones
     * son correctas se ejecuta el callback pasado como parámetro.
     *
     * @param successCallback callback de llamada si hay éxito.
     * @param failCallback callback de llamada si hay fracaso.
     */
    private fun doLocationChecks(successCallback: () -> Unit, failCallback: () -> Unit) {
        if(PermissionUtils.check(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)){ // Permisos
            if(LocationUtils.checkGPS(requireContext())){ // Comprobar GPS activado
               successCallback()
            } else {
                LocationUtils.createLocationSettingsAlertDialog(requireContext()).show() // Solicitar activación de GPS
                failCallback()
            }
        } else {
            requestLocationPermissions() // Solicitar permisos necesarios
            failCallback()
        }
    }

    /**
     * Registra los BroadCast Receivers en la Activity.
     */
    private fun registerReceivers(){
        // Receiver para las alarmas
        if(locationAlarmBroadCastReceiver == null)
            locationAlarmBroadCastReceiver = LocationAlarmBroadCastReceiver()
        requireActivity().registerReceiver(
                locationAlarmBroadCastReceiver,
                IntentFilter(ACTION_LOCATION_ALARM))

        // Receiver para las localizaciones registradas.
        if(locationReceiver == null)
            locationReceiver = LocationUpdateBroadcastReceiver()
        requireActivity().registerReceiver(
            locationReceiver,
            IntentFilter(Constants.ACTION_GET_LOCATION)
        )
    }

    /**
     * Desvincula los BroadCast Receivers de la Activity.
     */
    private fun unregisterReceivers(){
        requireActivity().unregisterReceiver(locationAlarmBroadCastReceiver)
        requireActivity().unregisterReceiver(locationReceiver)
    }

    /**
     * Se encarga de crear el Adapter para los objetos UserLocation.
     */
    private fun createLocationsAdapter(){
        userLocationAdapter = UserLocationAdapter(object : UserLocationAdapter.OnUserLocationItemClick{
            override fun onClick(userLocation: UserLocation) {
                LocationUtils.showLocationInMaps(
                    requireContext(),
                    userLocation,
                    19,
                    "Localización ${Utils.formatDate(userLocation.locationTimestamp, "dd/MM/yyyy HH:mm:ss")}")
            }
        })
    }

    /**
     * Se encarga de inicializar el RecyclerView para las localizaciones
     * con el LayoutManager y el Adapter correspondiente.
     */
    private fun initLocationsRecyclerView(){
        val manager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        binding.apply {
            this.recyclerViewTrackLocationInfo.layoutManager = manager
            this.recyclerViewTrackLocationInfo.adapter = userLocationAdapter
            userLocationAdapter.recyclerView = this.recyclerViewTrackLocationInfo // Attach to adapter
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FirstItemFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TrackerFragment().apply {
                arguments = Bundle().apply {
                }
            }

        /**
         * Id de la solicitud de permisos para la localización.
         */
        private const val LOCATION_PERMISSION_REQUEST_ID: Int = 100

        // TAG
        private const val TAG: String = "TrackLocationFragment"

        // Broadcast Receiver
        const val ACTION_LOCATION_ALARM = "LocationAlarm"
        const val EXTRA_LOCATION_ALARM_COMMAND = "LocationAlarmCommand"
    }
}