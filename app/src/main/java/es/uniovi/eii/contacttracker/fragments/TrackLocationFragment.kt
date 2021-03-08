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
    private var locationTracker: LocationTracker? = null


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

//        binding.btnPermisos.setOnClickListener {
//           context?.let {
//               if(ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED){
//
//                   requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_ID)
//               }
//           }
//            context?.let {
//               if(!FusedLocationTracker(it).test()){
//                   requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_ID)
//               }
//            }
//        }

        locationTracker?.setCallback(object : LocationUpdateCallback {
            override fun onLocationUpdate(location: Location) {
               val stringLoc = LocationUtils.format(location)
                Log.d("PRUEBA", stringLoc)
            }
        })

        binding.btnStart.setOnClickListener {
            locationTracker?.let {
                if(context != null){
                    if (PermissionUtils.check(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION)){
                        it.start(LocationUpdateMode.CALLBACK_MODE)
                    } else {
                        requestLocationPermissions()
                    }
                }
            }
        }

        binding.btnStop.setOnClickListener{
            locationTracker?.let {
                it.stop(LocationUpdateMode.CALLBACK_MODE)
            }
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
                    view?.let { Snackbar.make(it, "PERMISO CONCEDIDO", Snackbar.LENGTH_LONG).show() }
                }
            }
        }
    }

    private fun requestLocationPermissions(){
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_ID)
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

        private const val TAG: String = "TrackLocationFragment"
    }
}