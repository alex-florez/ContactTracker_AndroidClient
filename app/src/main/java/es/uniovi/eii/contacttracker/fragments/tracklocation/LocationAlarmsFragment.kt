package es.uniovi.eii.contacttracker.fragments.tracklocation

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.adapters.alarms.LocationAlarmAdapter
import es.uniovi.eii.contacttracker.databinding.FragmentLocationAlarmsBinding
import es.uniovi.eii.contacttracker.fragments.dialogs.timepicker.OnTimeSetListener
import es.uniovi.eii.contacttracker.fragments.dialogs.timepicker.TimePickerFragment
import es.uniovi.eii.contacttracker.model.LocationAlarm
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.util.PermissionUtils
import es.uniovi.eii.contacttracker.util.Utils
import es.uniovi.eii.contacttracker.viewmodels.LocationAlarmsViewModel
import org.w3c.dom.Text
import java.util.Date
import java.util.*

/**
 * Fragment para la gestión de las alarmas de localización.
 */
@AndroidEntryPoint
class LocationAlarmsFragment : Fragment() {

    /**
     * ViewBinding
     */
    private lateinit var binding: FragmentLocationAlarmsBinding

    /**
     * ViewModel
     */
    private val viewModel: LocationAlarmsViewModel by viewModels()

    /**
     * Adapter de alarmas de localización.
     */
    private lateinit var locationAlarmsAdapter: LocationAlarmAdapter

    /**
     * TimePickers de Material
     */
    private lateinit var startTimePicker: TimePickerFragment
    private lateinit var endTimePicker: TimePickerFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        createTimePickers()
        createAlarmsAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentLocationAlarmsBinding.inflate(inflater, container, false)

        setListeners()
        setObservers()
        initAlarmsRecycler()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Foco inicial en la hora de inicio
        binding.layoutCardLocationAlarm.txtStartAutoTracking.requestFocus()
        toggleLabelNoAlarms()
        viewModel.initAlarmPlaceHolders()

        // Comprobar ajustes de localizacion
        checkLocationSettings()
    }

    /**
     * Método que será invocado cuando
     * se pulse el botón para añadir una nueva alarma.
     */
    private fun addNewAlarm(){
        viewModel.addNewAlarm()
    }

    /**
     * Método que se encarga de crear los TimePickers de Material
     * para seleccionar las horas.
     */
    private fun createTimePickers(){
        startTimePicker = TimePickerFragment(object : OnTimeSetListener {
            override fun onTimeSet(hour: Int, minute: Int) {
                viewModel.setStartTime(Utils.getDate(hour, minute))
            }
        }, "Hora de inicio")

        endTimePicker = TimePickerFragment(object : OnTimeSetListener {
            override fun onTimeSet(hour: Int, minute: Int) {
                viewModel.setEndTime(Utils.getDate(hour, minute))
            }
        }, "Hora de fin")
    }

    /**
     * Crea e inicializa el Adapter para las Alarmas
     * de localización.
     */
    private fun createAlarmsAdapter(){
        locationAlarmsAdapter = LocationAlarmAdapter(object : LocationAlarmAdapter.OnRemoveAlarmClickListener {
            override fun onRemove(locationAlarm: LocationAlarm) { // ELIMINAR Alarma
                locationAlarm.id?.let { viewModel.deleteAlarm(locationAlarm) }
            }
        }, object : LocationAlarmAdapter.OnAlarmStateChangedListener {
            override fun onChanged(locationAlarm: LocationAlarm, isChecked: Boolean) { // Activar/Desactivar alarma
                locationAlarm.id?.let { viewModel.toggleAlarmState(locationAlarm, isChecked) }
            }
        })
        // IDs estables para reutilizar ViewHolders.
        locationAlarmsAdapter.setHasStableIds(true)
    }

    /**
     * Inicializa el recycler view que muestra
     * las alarmas de localización.
     */
    private fun initAlarmsRecycler(){
        val manager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        binding.recyclerViewLocationAlarms.apply {
            this.layoutManager = manager
            this.adapter = locationAlarmsAdapter
        }
    }

    /**
     * Establece los listeners para cada componente de la UI.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners(){
        // TextFields para las horas de Inicio y de Fin.
        binding.layoutCardLocationAlarm.txtStartAutoTracking.setOnClickListener{
           startTimePicker.show(requireActivity().supportFragmentManager, "StartTime")
        }

        binding.layoutCardLocationAlarm.txtEndAutoTracking.setOnClickListener{
            endTimePicker.show(requireActivity().supportFragmentManager, "EndTime")
        }

        binding.layoutCardLocationAlarm.btnAddLocationAlarm.setOnClickListener{
            addNewAlarm()
        }
    }

    /**
     * Método encargado de configurar los observers
     * para observar los datos del ViewModel.
     */
    private fun setObservers(){
        // PlaceHolder de hora de INICIO
        viewModel.starTime.observe(viewLifecycleOwner, {
            updateStartHour(it)
        })
        // PlaceHolder de hora de FIN
        viewModel.endTime.observe(viewLifecycleOwner, {
            updateEndHour(it)
        })
        // Lista de todas las alarmas programadas
        viewModel.getAllAlarms().observe(viewLifecycleOwner, {
            updateAlarmsAdapter(it)
        })
        // Flag de validez de horas de INICIO y de FIN
        viewModel.flagValidHours.observe(viewLifecycleOwner, { validHours ->
            if(!validHours){
                Snackbar.make(binding.root, getString(R.string.errorInvalidHours), Snackbar.LENGTH_LONG).let {
                    it.anchorView = requireActivity().findViewById(R.id.bottomNavigationView)
                    it.show()
                }
            }
        })
        // Flag de colisión de alarmas.
        viewModel.flagAlarmCollision.observe(viewLifecycleOwner, { collisions ->
            if(collisions){
                Snackbar.make(binding.root, getString(R.string.errorAlarmCollision), Snackbar.LENGTH_LONG).let {
                    it.anchorView = requireActivity().findViewById(R.id.bottomNavigationView)
                    it.show()
                }
            }
        })
    }

    /**
     * Actualiza los componentes de la UI con la
     * hora de INICIO pasada como parámetro.
     *
     * @param date hora de inicio.
     */
    private fun updateStartHour(date: Date) {
        binding.layoutCardLocationAlarm
                    .txtStartAutoTracking.setText(Utils.formatDate(date, "HH:mm"))
            // Establecer Horas y minutos en el TimePicker.
            startTimePicker.hours = Utils.getFromDate(date, Calendar.HOUR_OF_DAY)
            startTimePicker.minutes = Utils.getFromDate(date, Calendar.MINUTE)
    }

    /**
     * Actualiza los componentes de la UI con la
     * hora de FIN pasada como parámetro.
     *
     * @param date hora de fin.
     */
    private fun updateEndHour(date: Date) {
        binding.layoutCardLocationAlarm
                .txtEndAutoTracking.setText(Utils.formatDate(date, "HH:mm"))
        // Establecer Horas y minutos en el TimePicker.
        endTimePicker.hours = Utils.getFromDate(date, Calendar.HOUR_OF_DAY)
        endTimePicker.minutes = Utils.getFromDate(date, Calendar.MINUTE)
    }

    /**
     * Actualiza el adapter de alarmas de localización con la lista
     * pasada como parámetro.
     */
    private fun updateAlarmsAdapter(alarms: List<LocationAlarm>){
        locationAlarmsAdapter.submitList(alarms.toList())
        if(alarms.isEmpty())
            binding.txtLabelNoAlarms.visibility = TextView.VISIBLE
        else
            binding.txtLabelNoAlarms.visibility = TextView.GONE
    }

    /**
     * Cambia la visibilidad de la etiqueta de texto que indica
     * que no hay alarmas de localización si el adapter está vacío.
     */
    private fun toggleLabelNoAlarms(){
        if(locationAlarmsAdapter.currentList.isEmpty())
            binding.txtLabelNoAlarms.visibility = TextView.VISIBLE
        else
            binding.txtLabelNoAlarms.visibility = TextView.GONE
    }

    /**
     * Comprueba la configuración de la localización del
     * dispositivo. Comprueba que el GPS esté activado, y
     * también que se disponga de los permisos adecuados.
     */
    private fun checkLocationSettings(){
        val dialogBuilder = AlertDialog.Builder(requireContext())
        if(!PermissionUtils.check(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)){
            dialogBuilder.setTitle("Permiso de localización")
            dialogBuilder.setMessage("Recuerda que debes conceder los permisos de localización para poder obtener tu ubicación. De otro modo las alarmas programadas no funcionarán.")
            dialogBuilder.setPositiveButton("Ajustes de ubicación") { dialog, which ->
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + requireContext().packageName)).let {
                    it.addCategory(Intent.CATEGORY_DEFAULT)
                    startActivity(it)
                }
            }
            dialogBuilder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.cancel()
            }
            dialogBuilder.create().show()
        } else if (!LocationUtils.checkGPS(requireContext())){
            dialogBuilder.setTitle("Activar GPS")
            dialogBuilder.setMessage("Recuerda activar el GPS para poder rastrear tu ubicación. De otro modo las alarmas programadas no funcionarán.")
            dialogBuilder.setPositiveButton("Ajustes de ubicación") { dialog, which ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }

            dialogBuilder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.cancel()
            }
            dialogBuilder.create().show()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LocationAlarmsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                LocationAlarmsFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}