package es.uniovi.eii.contacttracker.fragments.history

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
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.FragmentMapsBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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

        setListeners()
        return binding.root
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
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
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
        val aviles = LatLng(43.5580, -5.9247)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(aviles, 14f))
    }
}