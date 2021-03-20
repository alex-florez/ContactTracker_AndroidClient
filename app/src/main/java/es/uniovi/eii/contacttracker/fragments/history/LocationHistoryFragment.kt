package es.uniovi.eii.contacttracker.fragments.history

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import es.uniovi.eii.contacttracker.adapters.UserLocationAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentHistoryBinding
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.viewmodels.LocationHistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * Fragmento para la opción de menú "Histórico".
 *
 * Representa la funcionalidad para visualizar y consultar la
 * historia de los recorridos del usuario, y las coordenadas registradas
 * en el tiempo.
 */
class LocationHistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    /**
     * ViewBinding
     */
    private lateinit var binding: FragmentHistoryBinding

    /**
     * ViewModel
     */
    private val viewModel: LocationHistoryViewModel by viewModels()

    /**
     * Adapter para las localizaciones
     */
    private lateinit var userLocationAdapter: UserLocationAdapter

    /**
     * Filtro de fecha seleccionado desde la interfaz.
     */
    private var dateFilter: Date = Date()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        userLocationAdapter = UserLocationAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        initRecyclerView()
        setListeners()

        viewModel.insertedUserLocationId.observe(this, {
            Snackbar.make(binding.root, "Nueva localización insertada: $it", Snackbar.LENGTH_LONG).show()
        })

        viewModel.getAllUserLocationsByDate(dateFilter).observe(this, {
            userLocationAdapter.submitList(it)
        })

        viewModel.affectedRows.observe(this, {
            Snackbar.make(binding.root, "Se han eliminado $it localizaciones", Snackbar.LENGTH_LONG).show()
        })
        return binding.root
    }


    /**
     * Configura e inicializa el RecyclerView con el adapter.
     */
    private fun initRecyclerView(){
        binding.recyclerViewUserLocations.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUserLocations.adapter = userLocationAdapter
    }

    /**
     * Configura e inicializa todos los listeners para los
     * componentes de la UI.
     */
    private fun setListeners(){
        // Botón de eliminar
        binding.btnDeleteAllLocations.setOnClickListener {
            viewModel.deleteAllUserLocations()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                LocationHistoryFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }

        private const val TAG = "LocationHistoryFragment"
    }
}