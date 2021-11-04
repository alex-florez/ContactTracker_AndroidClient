package es.uniovi.eii.contacttracker.fragments.riskcontacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.FragmentRiskContactMapBinding
import es.uniovi.eii.contacttracker.model.Point
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.util.DateUtils

/**
 * Contacto de Riesgo.
 */
private const val ARG_RISK_CONTACT = "argRiskContact"

/**
 * Fragmento con un mapa de Google Maps que refleja los
 * Tramos de Contactos de Riesgo obtenidos mediante la comprobación.
 */
@AndroidEntryPoint
class RiskContactMapFragment : Fragment(), OnMapReadyCallback {
    /**
     * Contacto de Riesgo.
     */
    private lateinit var riskContact: RiskContact

    /**
     * ViewBinding
     */
    private lateinit var binding: FragmentRiskContactMapBinding

    /**
     * Referencia al mapa.
     */
    private lateinit var map: GoogleMap

    /* Argumentos */
    private val args: RiskContactMapFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRiskContactMapBinding.inflate(inflater, container, false)
        // Argumentos
        riskContact = args.riskContact

        // Enviar datos al XML
        val minSecs = DateUtils.getMinuteSecond(riskContact.exposeTime)
        binding.rc = riskContact
        binding.exposeTimeMins = minSecs[0]
        binding.exposeTimeSecs = minSecs[1]

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
        val userLocations = mutableListOf<Point>()
        val positiveLocations = mutableListOf<Point>()
        // Crear listas con las localizaciones del usuario y del positivo.
        riskContact.contactLocations.forEach { contactLocation ->
            userLocations.add(contactLocation.userContactPoint)
            positiveLocations.add(contactLocation.positiveContactPoint)
        }
        /* Dibujar Localizaciones del usuario */
        drawPolyline(userLocations, requireContext().getColor(R.color.blue1))
        /* Dibujar localizciones del positivo */
        drawPolyline(positiveLocations, requireContext().getColor(R.color.red1))

        /* Dibujar marcadores */
        drawMarkers(userLocations, BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        drawMarkers(positiveLocations, BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))

        if(userLocations.isNotEmpty()){
            /* Mover la cámara */
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(userLocations[0].lat, userLocations[0].lng), 20f))
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
    private fun drawPolyline(contactPoints: List<Point>, color: Int) {
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
    }

    /**
     * Dibuja un marcador de inicio y de fin en las localizaciones primera
     * y última respectivamente de la lista pasada como parámetro y con el
     * icono indicado.
     *
     * @param locations Lista de localizaciones.
     * @param icon Icono del marcador.
     */
    private fun drawMarkers(locations: List<Point>, icon: BitmapDescriptor) {
        val points = locations.map {
            LatLng(it.lat, it.lng)
        }
        if(points.isNotEmpty()) {
            // Marcador de inicio
            map.addMarker(MarkerOptions()
                .position(points[0])
                .icon(icon))
            // Marcador de fin
            map.addMarker(MarkerOptions()
                .position(points.last())
                .icon(icon))
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