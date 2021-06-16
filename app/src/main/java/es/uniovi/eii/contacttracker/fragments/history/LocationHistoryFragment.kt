package es.uniovi.eii.contacttracker.fragments.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.adapters.locations.UserLocationAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentHistoryBinding
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.model.UserLocationList
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.util.Utils
import es.uniovi.eii.contacttracker.viewmodels.LocationHistoryViewModel
import java.util.Date

/**
 * Fragmento para la opción de menú "Histórico".
 *
 * Representa la funcionalidad para visualizar y consultar la
 * historia de los recorridos del usuario, y las coordenadas registradas
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
     * Fecha seleccionada.
     */
    private lateinit var selectedDate: Date


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
                    "Localización ${Utils.formatDate(userLocation.locationTimestamp, "dd/MM/yyyy HH:mm:ss")}")
            }
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        initRecyclerView()
        setDatePicker()
        setListeners()

        // Llamada al ViewModel para obtener las localizaciones por fecha.
        Transformations.switchMap(viewModel.dateFilter){date ->
            selectedDate = date
            viewModel.getAllUserLocationsByDate(date)
        }.observe(viewLifecycleOwner, { locationList ->
            userLocationAdapter.addLocations(locationList)
            binding.recyclerViewUserLocations.smoothScrollToPosition(0) // Hacer scroll en el recycler
            toggleNoLocationsLabel(locationList) // Etiqueta de lista vacía
            showNumberOfLocations(locationList.size) // Caja de información general
        })

        // Observer para la eliminación de localizaciones
        viewModel.deletedRows.observe(viewLifecycleOwner, {
            Snackbar.make(binding.root, "Se han eliminado $it localizaciones", Snackbar.LENGTH_LONG).let { snack ->
                snack.anchorView = requireActivity().findViewById(R.id.bottomNavigationView)
                snack.show()
            }
        })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateSelectedDate(Date())
        viewModel.deletedRows.value// Restaurar LiveData de localizaciones eliminadas
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
            showDatePicker()
        }

        // Botón para mostrar el mapa
        binding.btnShowMap.setOnClickListener{
            showMap()
        }
    }

    /**
     * Comprueba si hay datos para mostrar en el adapter de
     * localizaciones y si no hay muestra el label de texto
     * correspondiente.
     *
     * @param locationList lista de localizaciones.
     */
    private fun toggleNoLocationsLabel(locationList: List<UserLocation>){
        if(locationList.isEmpty())
            binding.labelHistoryNoLocations.visibility = TextView.VISIBLE
        else
            binding.labelHistoryNoLocations.visibility = TextView.GONE
    }

    /**
     * Muestra el número de localizaciones registradas
     * hasta el momento.
     */
    private fun showNumberOfLocations(number: Int){
        if(number == 0){
            binding.historyDataBox.visibility = View.INVISIBLE
        } else {
            binding.historyDataBox.visibility = View.VISIBLE
            binding.txtNumberOfLocations.text = number.toString()
        }
    }

    /**
     * Configura e inicializa el Material DatePicker.
     */
    private fun setDatePicker(){
        val actualDate = Date()
        datePickerBuilder.setTitleText(R.string.history_date_picker_title)
        datePickerBuilder.setSelection(actualDate.time)
        selectedDate = actualDate

        datePicker = datePickerBuilder.build()
        // Listener
        datePicker.addOnPositiveButtonClickListener {
            updateSelectedDate(Date(it as Long))
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
     * Se encarga de actualizar el filtro con la fecha
     * seleccionada mediante el DatePicker.
     */
    private fun updateSelectedDate(date: Date){
        viewModel.dateFilter.value = date
        binding.txtInputEditTextHistoryDate.setText(Utils.formatDate(date, "dd/MM/YYYY"))
    }

    /**
     * Método invocado para mostrar un mapa de Google Maps
     * con las localizaciones.
     */
    private fun showMap(){
        // Recuperar las localizaciones de la fecha seleccionada
        viewModel.getAllUserLocationsByDate(selectedDate).observe(viewLifecycleOwner) { locations ->
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.historyPlaceholder, MapsFragment.newInstance(UserLocationList(locations)))
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