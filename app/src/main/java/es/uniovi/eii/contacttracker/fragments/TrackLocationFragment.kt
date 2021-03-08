package es.uniovi.eii.contacttracker.fragments

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import es.uniovi.eii.contacttracker.databinding.FragmentTrackLocationBinding
import es.uniovi.eii.contacttracker.location.LocationTracker
import es.uniovi.eii.contacttracker.location.LocationUpdateMode
import es.uniovi.eii.contacttracker.location.callbacks.LocationUpdateCallback
import es.uniovi.eii.contacttracker.location.trackers.FusedLocationTracker
import es.uniovi.eii.contacttracker.location.trackers.LocationManagerTracker
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.util.PermissionUtils

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * Fragment que representa la opción de MENÚ que contiene la
 * funcionalidad para iniciar y pausar el rastreo de ubicación.
 */
class TrackLocationFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    /**
     * View Binding
     */
    private lateinit var binding: FragmentTrackLocationBinding

    /**
     * LocationTracker.
     */
    private lateinit var locationTracker: LocationTracker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        context?.let { locationTracker = FusedLocationTracker(it)}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackLocationBinding.inflate(inflater, container, false)

        locationTracker.setCallback(object : LocationUpdateCallback {
            override fun onLocationUpdate(location: Location) {
               val stringLoc = LocationUtils.format(location)
                Log.d("CallBackLocationTracker", stringLoc)
            }
        })

        binding.btnStart.setOnClickListener {
           startLocationTracker()
        }

        binding.btnStop.setOnClickListener{
            stopLocationTracker()
        }

        return binding.root
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_ID -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startLocationTracker()
                    view?.let { Snackbar.make(it, "PERMISO CONCEDIDO", Snackbar.LENGTH_LONG).show() }
                }
            }
        }
    }

    /**
     * Método encargado de solicitar los permisos necesarios
     * para la localización.
     */
    private fun requestLocationPermissions(){
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_ID)
    }

    /**
     * Método para iniciar el rastreador de ubicación, comprobando los permisos
     * y la configuración necesaria.
     */
    private fun startLocationTracker(){
        context?.let {
            if(PermissionUtils.check(it, android.Manifest.permission.ACCESS_FINE_LOCATION)){ // Permisos
                if(LocationUtils.checkGPS(it)){ // Configuración
                    locationTracker.start(LocationUpdateMode.CALLBACK_MODE)
                } else {
                    LocationUtils.createLocationSettingsAlertDialog(it).show() // Solicitar activación de GPS
                }
            } else {
                requestLocationPermissions() // Solicitar permisos necesarios
            }
        }
    }

    /**
     * Método encargado de detener el rastreador de ubicación.
     */
    private fun stopLocationTracker(){
        locationTracker.stop(LocationUpdateMode.CALLBACK_MODE)
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
            TrackLocationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
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