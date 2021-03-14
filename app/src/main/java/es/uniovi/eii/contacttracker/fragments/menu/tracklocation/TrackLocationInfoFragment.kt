package es.uniovi.eii.contacttracker.fragments.menu.tracklocation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.adapters.UserLocationAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentTrackLocationInfoBinding
import es.uniovi.eii.contacttracker.model.UserLocation

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * Fragmento que presenta al usuario la información en
 * tiempo real del rastreo de ubicación.
 */
class TrackLocationInfoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    /**
     * ViewBinding
     */
    private lateinit var binding: FragmentTrackLocationInfoBinding

    /**
     * Adapter para los objetos UserLocation.
     */
    private lateinit var userLocationAdapter: UserLocationAdapter

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
        binding = FragmentTrackLocationInfoBinding.inflate(inflater, container, false)

        initRecyclerView() // Inicializar RecyclerView
        return binding.root
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
        binding.recyclerViewTrackLocationInfo.layoutManager = manager
        binding.recyclerViewTrackLocationInfo.adapter = userLocationAdapter
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
            TrackLocationInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}