package es.uniovi.eii.contacttracker.fragments.riskcontacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.FragmentRiskContactMapBinding
import es.uniovi.eii.contacttracker.model.Point
import es.uniovi.eii.contacttracker.model.RiskContact

/**
 * Contacto de Riesgo.
 */
private const val ARG_RISK_CONTACT = "argRiskContact"

/**
 * Fragmento con un mapa de Google Maps para reflejar los
 * Tramos de Contactos de Riesgo obtenidos mediante la comprobación.
 */
@AndroidEntryPoint
class RiskContactMapFragment : Fragment(), OnMapReadyCallback {
    /**
     * Contacto de Riesgo.
     */
    private var riskContact: RiskContact? = null

    /**
     * ViewBinding
     */
    private lateinit var binding: FragmentRiskContactMapBinding

    /**
     * Referencia al mapa.
     */
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            riskContact = it.getParcelable(ARG_RISK_CONTACT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRiskContactMapBinding.inflate(inflater, container, false)
        // Inicializar el mapa
        val mapFragment: SupportMapFragment = childFragmentManager
            .findFragmentById(R.id.riskContactMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return binding.root
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        this.map.mapType = GoogleMap.MAP_TYPE_SATELLITE
        // Dibujar el contacto de riesgo en el mapa.
        drawContact()
    }

    /**
     * Se encarga de dibujar los TRAMOS de contacto
     * de riesgo en el mapa.
     */
    private fun drawContact(){
        riskContact?.let {
            val userLocations = mutableListOf<Point>()
            val positiveLocations = mutableListOf<Point>()
            // Crear listas con las localizaciones del usuario y del positivo.
            it.contactLocations.forEach { contactLocation ->
                userLocations.add(contactLocation.userContactPoint)
                positiveLocations.add(contactLocation.positiveContactPoint)
            }
            /* Dibujar Localizaciones del usuario */
            drawLocations(userLocations, requireContext().getColor(R.color.blue1))
            /* Dibujar localizciones del positivo */
            drawLocations(positiveLocations, requireContext().getColor(R.color.red1))
            if(userLocations.isNotEmpty()){
                /* Mover la cámara */
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(userLocations[0].lat, userLocations[0].lng), Constants.DEFAULT_ZOOM))
            }
        }
    }

    /**
     * Dibuja en el mapa el tramo representado por la lista de
     * puntos de contactos de riesgo mediante una polilínea del
     * color indicado.
     *
     * @param contactPoints Lista de puntos de contacto.
     * @param color Color en el que se desea dibujar la línea.
     *
     */
    private fun drawLocations(contactPoints: List<Point>, color: Int) {
        // Crear la lista de puntos
        val points = mutableListOf<LatLng>()
        contactPoints.forEach {
            points.add(LatLng(it.lat, it.lng))
        }
        // Dibujar polilínea
        val path = map.addPolyline(PolylineOptions()
            .addAll(points))
        path.width = 12f
        path.color = color
        // Dibujar marcador de inicio y de fin
        if(points.isNotEmpty()){
            val startMarker = map.addMarker(MarkerOptions()
                .position(points[0]))
            val endMarker = map.addMarker(MarkerOptions()
                .position(points[points.size-1]))
        }
    }

    companion object {
        /**
         * Método estático empleado como Factory Method para construir una
         * instancia de este fragment indicando los parámetros adecuados.
         */
        @JvmStatic
        fun newInstance(riskContact: RiskContact) =
            RiskContactMapFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_RISK_CONTACT, riskContact)
                }
            }
    }
}