package es.uniovi.eii.contacttracker.fragments.tracklocation

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
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.FragmentTrackerMapsBinding
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.util.LocationUtils


/**
 * Fragmento que contiene un mapa de Google Maps para visualizar
 * un itinerario de localizaciones mediante el trazado de una línea,
 * además de recibir actualizaciones en tiempo real de las localizaciones
 * que se van registrando durante el rastreo.
 */
@AndroidEntryPoint
class TrackerMapFragment : Fragment(), OnMapReadyCallback {

    /**
     * Referencia al mapa.
     */
    private lateinit var map: GoogleMap

    /**
     * Map View
     */
    private lateinit var mapView: MapView

    /**
     * View binding.
     */
    private lateinit var binding: FragmentTrackerMapsBinding

    /**
     * Animaciones de los FABs para seleccionar la capa.
     */
    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim) }

    /* Flag para los FABs */
    private var fabClicked: Boolean = false

    /* Flag para mostrar/ocultar los marcadores */
    private var markersEnabled: Boolean = true

    /**
     * Listado de localizaciones que conforman el itinerario.
     */
    private var locations: List<UserLocation> = listOf()

    /**
     * Listado de marcadores de Google Maps que representan las localizaciones.
     */
    private val markers = mutableListOf<Marker>()

    /**
     * Polyline que representa la línea que une las localizaciones.
     */
    private lateinit var path: Polyline

    /**
     * Broadcast receiver para recibir las localizaciones en tiempo real.
     */
    private var newLocationBroadcastReceiver: NewLocationBroadcastReceiver? = null

    /**
     * Broadcast receiver para las localizaciones obtenidas
     * mediante el servicio de localización actualmente activo.
     */
    inner class NewLocationBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            // Recuperar nueva localización
            val userLocation: UserLocation? = intent?.getParcelableExtra(Constants.EXTRA_LOCATION)
            userLocation?.let { drawNewLocation(it) }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTrackerMapsBinding.inflate(inflater, container, false)
        // Inicializar Map View
        initMap(savedInstanceState)
        setListeners()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        registerReceiver()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        unregisterReceiver()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    /**
     * Inicializa la vista con el mapa de Google Maps.
     */
    private fun initMap(savedInstanceState: Bundle?) {
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
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
            // FAB para terreno
            fabTypeTerrain.setOnClickListener{
                changeMapType(GoogleMap.MAP_TYPE_TERRAIN)
            }

            // Checkbox para ocultar marcadores
            chipShowMarkers.setOnCheckedChangeListener { _, isChecked ->
                toggleMarkers(isChecked)
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
     * Alterna entre activar o desactivar los marcadores
     * del mapa.
     */
    private fun toggleMarkers(flag: Boolean) {
        markersEnabled = flag
        markers.forEach {marker -> marker.isVisible = flag}
    }

    /**
     * Gestiona la visibilidad de los Fab buttons.
     */
    private fun setFabVisibility(clicked: Boolean){
        if(!clicked){
            binding.fabTypeNormal.visibility = View.VISIBLE
            binding.fabTypeSatellite.visibility = View.VISIBLE
            binding.fabTypeTerrain.visibility = View.VISIBLE
        } else {
            binding.fabTypeNormal.visibility = View.INVISIBLE
            binding.fabTypeSatellite.visibility = View.INVISIBLE
            binding.fabTypeTerrain.visibility = View.INVISIBLE
        }
    }

    /**
     * Gestiona las animaciones de los Fab buttons
     */
    private fun setFabAnimation(clicked: Boolean) {
        if(!clicked){
            binding.fabTypeNormal.startAnimation(fromBottom)
            binding.fabTypeSatellite.startAnimation(fromBottom)
            binding.fabTypeTerrain.startAnimation(fromBottom)
            binding.fabMapType.startAnimation(rotateOpen)
        } else {
            binding.fabTypeNormal.startAnimation(toBottom)
            binding.fabTypeSatellite.startAnimation(toBottom)
            binding.fabTypeTerrain.startAnimation(toBottom)
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
            binding.fabTypeTerrain.isClickable = true
        } else {
            binding.fabTypeNormal.isClickable = false
            binding.fabTypeSatellite.isClickable = false
            binding.fabTypeTerrain.isClickable = false
        }
    }

    /**
     * Cambia el tipo de mapa de Google Maps.
     *
     * @param mapType tipo de mapa.
     */
    private fun changeMapType(mapType: Int) {
        map.mapType = mapType
        onChangeMapTypeClicked()
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
        drawMarkers()
        // Dibujar PATH
        drawPath()
    }

    /**
     * Mueve la cámara hasta la posición de la última
     * localización registrada.
     */
    private fun setInitialLocation(){
        if(locations.isNotEmpty()){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom
                (LocationUtils.toLatLng(locations[0]), Constants.DEFAULT_ZOOM))
        }
    }

    /**
     * Agrega marcadores al mapa, uno para cada localización registrada.
     */
    private fun drawMarkers(){
        // Ordenar localizaciones por fecha
        val sortedLocations: List<UserLocation> = locations.sortedBy { location -> location.timestamp() }
        sortedLocations.forEach { location ->
            val latLng = LocationUtils.toLatLng(location)
            val marker = map.addMarker(MarkerOptions()
                .title(location.timestamp().toString())
                .position(latLng))
            marker?.let { markers.add(marker) } // Guardar marcador
        }
    }

    /**
     * Traza la ruta mediante una línea uniendo las localizaciones
     * señaladas con marcadores.
     */
    private fun drawPath(){
        // Obtener lista de puntos LatLng
        val points = mutableListOf<LatLng>()
        locations.forEach {
            points.add(LocationUtils.toLatLng(it))
        }
        // Crear la polilínea
        path = map.addPolyline(PolylineOptions().addAll(points))
        // Estilo de la polilínea.
        path.width = 12f
        path.color = requireContext().getColor(R.color.red1)
    }

    /**
     * Método invocado cada vez que se recibe una nueva localización
     * en el broadcast receiver en tiempo real, procedente del
     * servicio de localización.
     */
    private fun drawNewLocation(location: UserLocation) {
        val latLng = LocationUtils.toLatLng(location)
        val newMarker = map.addMarker(MarkerOptions()
            .title(location.timestamp().toString())
            .position(latLng)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            .visible(markersEnabled))
        // Dibujar línea
        if(markers.isNotEmpty()){
            val polyline = map.addPolyline(PolylineOptions()
                .add(markers[markers.size-1].position, // Última posición
                    latLng)) // Posición actual.
            polyline.color = requireContext().getColor(R.color.deepBlue)
            // Mover la cámara a la nueva posición.
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        } else { // Primera localización
            // Mover la cámara y hacer zoom
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.DEFAULT_ZOOM))
        }
        // Guardar marcador
        newMarker?.let { markers.add(it) }
    }

    /**
     * Vincula el broadcast receiver para recibir localizaciones
     * en tiempo real.
     */
    private fun registerReceiver(){
        if(newLocationBroadcastReceiver == null)
            newLocationBroadcastReceiver = NewLocationBroadcastReceiver()
        requireActivity().registerReceiver(newLocationBroadcastReceiver,
                                            IntentFilter(Constants.ACTION_GET_LOCATION))
    }

    /**
     * Desvincula el receiver de coordenadas
     */
    private fun unregisterReceiver(){
        requireActivity().unregisterReceiver(newLocationBroadcastReceiver)
    }

    companion object {

        /**
         * Extra con las localizaciones.
         */
        private const val LOCATIONS = "userLocations"

        /**
         * FactoryMethod para crear el fragment.
         *
         * @param userLocations lista con las localizaciones del usuario.
         */
        @JvmStatic
        fun newInstance(userLocations: List<UserLocation>) =
            TrackerMapFragment().apply {
                val list = arrayListOf<UserLocation>()
                list.addAll(userLocations)
                arguments = Bundle().apply {
                    putParcelableArrayList(LOCATIONS, list)
                }
            }
    }

}