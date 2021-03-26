package es.uniovi.eii.contacttracker.fragments.tracklocation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.FragmentTrackerBinding
import es.uniovi.eii.contacttracker.fragments.dialogs.timepicker.OnTimeSetListener
import es.uniovi.eii.contacttracker.fragments.dialogs.timepicker.TimePickerFragment
import es.uniovi.eii.contacttracker.location.services.LocationForegroundService
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.util.PermissionUtils
import es.uniovi.eii.contacttracker.viewmodels.TrackerViewModel


/**
 * Fragment que representa la opción del RASTREADOR DE UBICACIÓN.
 *
 * Contiene la funcionalidad para iniciar y pausar el rastreo de ubicación
 * de manera manual, así como la funcionalidad para programar alarmas de rastreo
 * de ubicación.
 */
@AndroidEntryPoint
class TrackerFragment : Fragment() {

    /**
     * View Binding
     */
    private lateinit var binding: FragmentTrackerBinding

    /**
     * ViewModel
     */
    private val viewModel:TrackerViewModel by viewModels()

    /**
     * TimePickers de Material
     */
    private lateinit var startTimePicker: TimePickerFragment
    private lateinit var endTimePicker: TimePickerFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        createTimePickers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackerBinding.inflate(inflater, container, false)


        setListeners()
        return binding.root
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
                    view?.let { Snackbar.make(it, "PERMISO CONCEDIDO", Snackbar.LENGTH_LONG).show() }
                }
            }
        }
    }

    /**
     * Establece los listeners para cada componente de la UI.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners(){
        // Botones de rastreo manual
        binding.btnStartTracker.setOnClickListener {
            startLocationService()
        }

        binding.btnStopTracker.setOnClickListener{
            stopLocationService()
        }
        // Alarma de localización
        binding.layoutCardLocationAlarm.switchAutomaticTracking
                .setOnCheckedChangeListener{ btnView, isChecked ->
                    toggleAutomaticTracking(isChecked)
        }

        binding.layoutCardLocationAlarm.txtStartAutoTracking.setOnClickListener{
           startTimePicker.show(requireActivity().supportFragmentManager, "tag")
        }

        binding.layoutCardLocationAlarm.txtEndAutoTracking.setOnClickListener{
            endTimePicker.show(requireActivity().supportFragmentManager, "tag")
        }


        binding.layoutCardLocationAlarm.btnApplyAutoTracking.setOnClickListener {

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
     * Se encarga de inicializar el servicio de localización
     * en 1er plano para obtener actualizaciones de localización.
     */
    private fun startLocationService(){
        if(PermissionUtils.check(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)){ // Permisos
            if(LocationUtils.checkGPS(requireContext())){ // Configuración
               if(!LocationUtils.isLocationServiceRunning(requireContext())) { // Comprobar que el servicio no esté ya ejecutándose
                   sendCommandToLocationService(LocationForegroundService.ACTION_START_LOCATION_SERVICE)
               } else {
                   Log.d(TAG, "Ya se está ejecutando un servicio de localización")
               }
            } else {
                LocationUtils.createLocationSettingsAlertDialog(requireContext()).show() // Solicitar activación de GPS
            }
        } else {
            requestLocationPermissions() // Solicitar permisos necesarios
        }
    }

    /**
     * Método encargado de detener el rastreador de ubicación.
     */
    private fun stopLocationService(){
        if(LocationUtils.isLocationServiceRunning(requireContext())) { // Comprobar que el servicio se esté ejecutando.
            sendCommandToLocationService(LocationForegroundService.ACTION_STOP_LOCATION_SERVICE)
            Snackbar.make(binding.root, R.string.labelStoppedService, Snackbar.LENGTH_LONG).let{ // Mostrar Snackbar
                it.anchorView = requireActivity().findViewById(R.id.bottomNavigationView) // Mostrarlo encima del BottomNavView
                it.show()
            }
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
            ContextCompat.startForegroundService(requireContext(), it)
        }
    }

    /**
     * Método invocado cuando se pulsa sobre el Switch de la UI para
     * activar/desactivar el rastreo automático.
     */
    private fun toggleAutomaticTracking(activate: Boolean) {
        toggleAlarmCardState(activate)
        if(activate){

        } else {

        }
    }

    /**
     * Recibe como parámetro el estado para establecer los
     * componentes de la UI como activos o deshabilitados dentro
     * del Card de alarmas de localización.
     */
    private fun toggleAlarmCardState(state: Boolean){
        binding.layoutCardLocationAlarm.txtInputLayoutStartAutoTracking.isEnabled = state
        binding.layoutCardLocationAlarm.txtInputLayoutEndAutoTracking.isEnabled = state
        binding.layoutCardLocationAlarm.btnApplyAutoTracking.isEnabled = state
        binding.layoutCardLocationAlarm.labelCurrentAlarm.isEnabled = state
        binding.layoutCardLocationAlarm.labelValueCurrentAlarm.isEnabled = state
    }

    /**
     * Método que esconde el teclado de Android cuando
     * se pulsa sobre un EditText.
     */
    private fun hideKeyboard(v:View, event: MotionEvent): Boolean{
        v.onTouchEvent(event)
        val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
        return true
    }

    /**
     * Método que se encarga de crear los TimePickers de Material
     * para seleccionar las horas.
     */
    private fun createTimePickers(){
        startTimePicker = TimePickerFragment(object : OnTimeSetListener {
            override fun onTimeSet(hour: Int, minute: Int) {
                setAlarmStartTime(hour, minute)
            }
        }, "Hora de inicio")

        endTimePicker = TimePickerFragment(object : OnTimeSetListener {
            override fun onTimeSet(hour: Int, minute: Int) {
                setAlarmEndTime(hour, minute)
            }
        }, "Hora de fin")
    }

    /**
     * Establece la hora de inicio de la alarma.
     */
    private fun setAlarmStartTime(hour: Int, minute: Int){
        val time = "$hour:$minute"
        binding.layoutCardLocationAlarm.txtStartAutoTracking.setText(time)
    }

    /**
     * Establece la hora de fin de la alarma.
     */
    private fun setAlarmEndTime(hour: Int, minute: Int){
        val time = "$hour:$minute"
        binding.layoutCardLocationAlarm.txtEndAutoTracking.setText(time)
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
    }
}