package es.uniovi.eii.contacttracker.fragments.tracklocation

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.eii.contacttracker.adapters.UserLocationAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentTrackerInfoBinding
import es.uniovi.eii.contacttracker.location.receivers.LocationUpdateBroadcastReceiver
import es.uniovi.eii.contacttracker.model.UserLocation
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * Fragmento que se utiliza como Log y presenta al
 * usuario la información en tiempo real del rastreo de ubicación,
 * mientras el servicio de localización está activo.
 *
 */
class TrackerInfoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    /**
     * ViewBinding
     */
    private lateinit var binding: FragmentTrackerInfoBinding

    /**
     * Adapter para los objetos UserLocation.
     */
    lateinit var userLocationAdapter: UserLocationAdapter

    /**
     * BroadCast Receiver para actualizar el Adapter
     * con las actualizaciones de localización.
     */
    private var locationReceiver: LocationUpdateBroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        createAdapter() // Crear Adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackerInfoBinding.inflate(inflater, container, false)

        initRecyclerView() // Inicializar RecyclerView
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Registrar el Broadcast receiver para recibir las actualizaciones
        registerLocationReceiver()
    }

    override fun onPause() {
        super.onPause()
        // Desvincular BroadCast receiver
        unregisterLocationReceiver()
        userLocationAdapter.clearLocations()
    }

    /**
     * Método privado para registrar el BroadCast Receiver
     * para obtener las localizaciones.
     */
    private fun registerLocationReceiver(){
        if(locationReceiver == null)
            locationReceiver = LocationUpdateBroadcastReceiver(userLocationAdapter, this)
        activity?.registerReceiver(locationReceiver,
                IntentFilter((LocationUpdateBroadcastReceiver.ACTION_GET_LOCATION)))
    }


    /**
     * Método que desvincula el BroadCast Receiver de la Activity
     * para dejar de recibir actualizaciones de localización.
     */
    private fun unregisterLocationReceiver(){
        activity?.unregisterReceiver(locationReceiver)
    }

    /**
     * Se encarga de crear el Adapter
     * para los objetos UserLocation.
     */
    private fun createAdapter(){
        userLocationAdapter = UserLocationAdapter()
    }

    /**
     * Se encarga de actualizar el contenido del adapter
     * con la nueva lista de Localizaciones de Usuario.
     */
    private fun updateAdapter(userLocations: List<UserLocation>){
        userLocationAdapter.submitList(userLocations)
    }

    /**
     * Se encarga de inicializar el RecyclerView
     * con el LayoutManager y el Adapter corresppondiente.
     */
    private fun initRecyclerView(){
        val manager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        binding.apply {
            this.recyclerViewTrackLocationInfo.layoutManager = manager
            this.recyclerViewTrackLocationInfo.adapter = userLocationAdapter
            userLocationAdapter.recyclerView = this.recyclerViewTrackLocationInfo // Attach to adapter
        }
    }

    /**
     * Cambia la visibilidad de la etiqueta de texto que indica
     * que no hay localizaciones si el adapter está vacío.
     */
    fun toggleLabelNoLocations(){
        if(userLocationAdapter.currentList.isEmpty())
            binding.labelNoLocations.visibility = TextView.VISIBLE
        else
            binding.labelNoLocations.visibility = TextView.GONE
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecondItemFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TrackerInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        private const val TAG = "TrackerInfoFragment"
    }


}