package es.uniovi.eii.contacttracker.fragments.history

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.findFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.FragmentMapsBinding
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.model.UserLocationList
import es.uniovi.eii.contacttracker.util.LocationUtils


// Parámetro del fragmento.
private const val LOCATIONS = "userLocations"

/**
 * Fragmento que contiene un mapa de Google Maps
 * para visualizar los itinerarios del usuario, es decir,
 * el conjunto de coordenadas registradas.
 */
@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {

    /**
     * Referencia al mapa.
     */
    private lateinit var map: GoogleMap

    /**
     * View binding.
     */
    private lateinit var binding: FragmentMapsBinding

    /**
     * Animaciones de los FABs
     */
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim) }

    /* Flag para los FABs */
    private var fabClicked: Boolean = false

    /**
     * Listado de localizaciones.
     */
    private var locations: UserLocationList? = null

    /**
     * Broadcast receiver para las coordenadas
     * en tiempo real.
     */
    private var newLocationBroadcastReceiver: NewLocationBroadcastReceiver? = null

    /**
     * Broadcast receiver para las localizaciones obtenidas
     * mediante el servicio de localización activo.
     */
    inner class NewLocationBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            // Recuperar nueva localización
            val userLocation: UserLocation? = intent?.getParcelableExtra(Constants.EXTRA_LOCATION)
            userLocation?.let { addNewLocation(it) }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        // Inicializar Map Fragment
        val mapFragment: SupportMapFragment = childFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment

        // Notificar cuando el mapa esté listo.
        mapFragment.getMapAsync(this)

        // Recuperar localizaciones
        locations = arguments?.getParcelable(LOCATIONS)

        setListeners()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        registerReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver()
    }

    /**
     * Establece los listeners para los componentes de la UI.
     */
    private fun setListeners(){
        binding.apply {
            // FAB principal para cambiar el tipo de mapa.
            fabMapType.setOnClickListener{
                onChangeMapTypeClicked()
            }
            // FAB para mapa normal
            fabTypeNormal.setOnClickListener{
               changeMapType(GoogleMap.MAP_TYPE_NORMAL)
            }

            // FAB para mapa satélite
            fabTypeSatellite.setOnClickListener{
               changeMapType(GoogleMap.MAP_TYPE_SATELLITE)
            }
        }
    }

    /**
     * Método invocado al pulsar sobre el botón de cambio de tipo de mapa.
     */
    private fun onChangeMapTypeClicked(){
        setFabVisibility(fabClicked)
        setFabAnimation(fabClicked)
        setFabClickable(fabClicked)
        fabClicked = !fabClicked
    }

    /**
     * Gestiona la visibilidad de los Fab buttons.
     */
    private fun setFabVisibility(clicked: Boolean){
        if(!clicked){
            binding.fabTypeNormal.visibility = View.VISIBLE
            binding.fabTypeSatellite.visibility = View.VISIBLE
        } else {
            binding.fabTypeNormal.visibility = View.INVISIBLE
            binding.fabTypeSatellite.visibility = View.INVISIBLE
        }
    }

    /**
     * Gestiona las animaciones de los Fab buttons
     */
    private fun setFabAnimation(clicked: Boolean) {
        if(!clicked){
            binding.fabTypeNormal.startAnimation(fromBottom)
            binding.fabTypeSatellite.startAnimation(fromBottom)
            binding.fabMapType.startAnimation(rotateOpen)
        } else {
            binding.fabTypeNormal.startAnimation(toBottom)
            binding.fabTypeSatellite.startAnimation(toBottom)
            binding.fabMapType.startAnimation(rotateClose)
        }
    }

    /**
     * Gestiona la clickabilidad de los fabs.
     */
    private fun setFabClickable(clicked: Boolean) {
        if(!clicked){
            binding.fabTypeNormal.isClickable = true
            binding.fabTypeSatellite.isClickable = true
        } else {
            binding.fabTypeNormal.isClickable = false
            binding.fabTypeSatellite.isClickable = false
        }
    }

    /**
     * Cambia el tipo de mapa de Google Maps.
     *
     * @param mapType tipo de mapa.
     */
    private fun changeMapType(mapType: Int) {
        map.mapType = mapType
    }

    companion object {
        /**
         * FactoryMethod para crear el fragment.
         *
         * @param userLocations lista con las localizaciones del usuario.
         */
        @JvmStatic
        fun newInstance(userLocations: UserLocationList) =
            MapsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(LOCATIONS, userLocations)
                }
            }
    }

    /**
     * Callback de mapa listo.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        // Establecer posisición inicial
        setInitialLocation()
        // Añadir marcadores
        addMarkers()
    }

    /**
     * Mueve la cámara hasta la posición inicial.
     */
    private fun setInitialLocation(){
        val start = locations?.getInitialLocation()
        start?.apply {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), Constants.DEFAULT_ZOOM))
        }
    }

    /**
     * Agrega marcadores al mapa, uno para cada localización
     * registrada.
     */
    private fun addMarkers(){
        locations?.let {
            it.locations?.forEach { location ->
                val latLng = LocationUtils.toLatLng(location)
                val marker = map.addMarker(MarkerOptions()
                    .title(location.locationTimestamp.toString())
                    .position(latLng))
            }
        }
    }

    /**
     * Método invocado cada vez que se recibe una nueva localización
     * en el broadcast receiver en tiempo real, procedente del
     * servicio de localización.
     */
    private fun addNewLocation(location: UserLocation) {
        val latLng = LocationUtils.toLatLng(location)
        val newMarker = map.addMarker(MarkerOptions()
            .title(location.locationTimestamp.toString())
            .position(latLng)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)))
        // Mover la cámara a la nueva posición.
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.DEFAULT_ZOOM))
    }

    /**
     * Se encarga de mover la camára a la posición
     * especificada por LatLng.
     *
     * @param latLng Latitud y Longitud
     */
    private fun moveCameraTo(latLng: LatLng) {

    }

    /**
     * Vincula el broadcast receiver para recibir localizaciones
     * en tiempo real.
     */
    private fun registerReceiver(){
        if(newLocationBroadcastReceiver == null)
            newLocationBroadcastReceiver = NewLocationBroadcastReceiver()
        requireActivity()
            .registerReceiver(newLocationBroadcastReceiver,
                IntentFilter(Constants.ACTION_GET_LOCATION))
    }

    /**
     * Desvincula el receiver de coordenadas
     */
    private fun unregisterReceiver(){
        requireActivity().unregisterReceiver(newLocationBroadcastReceiver)
    }
}