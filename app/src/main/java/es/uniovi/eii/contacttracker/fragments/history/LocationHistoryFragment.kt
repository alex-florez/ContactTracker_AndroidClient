package es.uniovi.eii.contacttracker.fragments.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.adapters.locations.UserLocationAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentHistoryBinding
import es.uniovi.eii.contacttracker.fragments.maps.MapsFragment
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.util.AndroidUtils
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.util.DateUtils
import es.uniovi.eii.contacttracker.viewmodels.LocationHistoryViewModel
import java.util.Date

/**
 * Fragmento para la opción de menú "Histórico".
 *
 * Representa la funcionalidad para visualizar y consultar la
 * historia de los itinerarios del usuario, y las coordenadas registradas
 * en el tiempo.
 */
@AndroidEntryPoint
class LocationHistoryFragment : Fragment() {

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
     * Material DatePicker
     */
    private val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
    private lateinit var datePicker: MaterialDatePicker<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        // Adapter
        userLocationAdapter = UserLocationAdapter(object : UserLocationAdapter.OnUserLocationItemClick {
            override fun onClick(userLocation: UserLocation) {
                // Mostrar localización en un mapa
                LocationUtils.showLocationInMaps(
                    requireContext(),
                    userLocation,
                    19,
                    "Localización ${DateUtils.formatDate(userLocation.timestamp(), "dd/MM/yyyy HH:mm:ss")}")
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.du = DateUtils

        initRecyclerView()
        setDatePicker()
        setListeners()
        setObservers()


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateSelectedDate(Date()) // Establecer la fecha actual como seleccionada.
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
        binding.btnDeleteLocationsByDate.setOnClickListener {
            deleteUserLocations()
        }

        // Input Text para el filtro de fecha
        binding.txtInputEditTextHistoryDate.setOnClickListener {
            if(!datePicker.isVisible)
                showDatePicker()
        }

        // Botón para mostrar el mapa
        binding.btnShowMap.setOnClickListener{
            showMap()
        }
    }

    /**
     * Inicializa y configura los observers para escuchar los
     * eventos de los LiveData del ViewModel.
     */
    private fun setObservers() {
        viewModel.apply {
            locations.observe(viewLifecycleOwner) { locationList ->
                // Comprobar el scroll
                userLocationAdapter.submitList(locationList) {
                    binding.recyclerViewUserLocations.scrollToPosition(0)
                }
            }

            // Eliminación de localizaciones
            deletedRows.observe(viewLifecycleOwner, {
                AndroidUtils.snackbar(getString(R.string.deleteLocationsText, it), Snackbar.LENGTH_LONG,
                    binding.root, requireActivity())
            })
        }
    }

    /**
     * Configura e inicializa el Material DatePicker.
     */
    private fun setDatePicker(){
        val actualDate = Date()
        datePickerBuilder.setTitleText(R.string.history_date_picker_title)
        datePickerBuilder.setSelection(actualDate.time)
        datePicker = datePickerBuilder.build()
        // Listener de selección de fecha.
        datePicker.addOnPositiveButtonClickListener { date ->
            updateSelectedDate(Date(date as Long))
        }
    }

    /**
     * Muestra el DatePicker de Material para seleccionar
     * una fecha.
     */
    private fun showDatePicker(){
        datePicker.show(requireActivity().supportFragmentManager, datePicker.toString())
    }

    /**
     * Se encarga de eliminar las localizaciones del usuario cuya
     * fecha coincide con la seleccionada en el EditText.
     */
    private fun deleteUserLocations(){
        viewModel.dateFilter.value?.let {
            viewModel.deleteUserLocationsByDate(it)
        }
    }

    /**
     * Se encarga de actualizar el filtro de fecha del ViewModel
     * con la fecha pasada como parámetro.
     */
    private fun updateSelectedDate(date: Date){
        viewModel.setDateFilter(date)
    }

    /**
     * Método invocado para mostrar un mapa de Google Maps
     * con las localizaciones.
     */
    private fun showMap(){
        // Recuperar las localizaciones de la fecha seleccionada
        viewModel.locations.value?.let {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.main_fragment_container, MapsFragment.newInstance(it))
                .addToBackStack("MapsFragment")
                .commit()
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
                    }
                }

        private const val TAG = "LocationHistoryFragment"
    }
}