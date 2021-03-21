package es.uniovi.eii.contacttracker.fragments.tracklocation

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.databinding.FragmentTrackerBinding
import es.uniovi.eii.contacttracker.location.services.LocationForegroundService
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.util.PermissionUtils


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
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
    private fun setListeners(){
        binding.btnStartTracker.setOnClickListener {
            startLocationService()
        }

        binding.btnStopTracker.setOnClickListener{
            stopLocationService()
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
                sendCommandToLocationService(LocationForegroundService.ACTION_START_LOCATION_SERVICE)
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
        sendCommandToLocationService(LocationForegroundService.ACTION_STOP_LOCATION_SERVICE)
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