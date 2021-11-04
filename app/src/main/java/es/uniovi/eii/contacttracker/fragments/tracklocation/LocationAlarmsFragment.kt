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
import es.uniovi.eii.contacttracker.model.Error
import es.uniovi.eii.contacttracker.location.alarms.LocationAlarm
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.util.AndroidUtils
import es.uniovi.eii.contacttracker.util.DateUtils
import es.uniovi.eii.contacttracker.util.ValueWrapper
import es.uniovi.eii.contacttracker.viewmodels.LocationAlarmsViewModel
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
        createTimePickers()
        createAlarmsAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentLocationAlarmsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.du = DateUtils

        setListeners()
        setObservers()
        initAlarmsRecycler()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Foco inicial en la hora de inicio
        binding.layoutCardLocationAlarm.txtStartAutoTracking.requestFocus()
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
                viewModel.setStartTime(DateUtils.getDate(hour, minute))
            }
        }, "Hora de inicio")

        endTimePicker = TimePickerFragment(object : OnTimeSetListener {
            override fun onTimeSet(hour: Int, minute: Int) {
                viewModel.setEndTime(DateUtils.getDate(hour, minute))
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
        binding.apply {
            // TextFields para las horas de Inicio y de Fin.
            layoutCardLocationAlarm.txtStartAutoTracking.setOnClickListener{
                startTimePicker.show(requireActivity().supportFragmentManager, "StartTime")
            }
            layoutCardLocationAlarm.txtEndAutoTracking.setOnClickListener{
                endTimePicker.show(requireActivity().supportFragmentManager, "EndTime")
            }
            // Botón para añadir una nueva alarma
            layoutCardLocationAlarm.btnAddLocationAlarm.setOnClickListener{
                addNewAlarm()
            }
        }
    }

    /**
     * Método encargado de configurar los observers
     * para observar los datos del ViewModel.
     */
    private fun setObservers(){
        viewModel.apply {
            // PlaceHolder de hora de INICIO
            starTime.observe(viewLifecycleOwner, {
                updateStartHour(it)
            })

            // PlaceHolder de hora de FIN
            endTime.observe(viewLifecycleOwner, {
                updateEndHour(it)
            })

            // Lista de todas las alarmas programadas
            alarms.observe(viewLifecycleOwner, {
                updateAlarmsAdapter(it)
            })

            // Posible error al insertar una nueva alarma
            alarmSetError.observe(viewLifecycleOwner) { stringID ->
                AndroidUtils.snackbar(getString(stringID), Snackbar.LENGTH_LONG,
                    binding.root, requireActivity())
            }
        }

    }

    /**
     * Actualiza el campo de texto de la hora de inicio con la
     * hora de INICIO pasada como parámetro.
     *
     * @param date hora de inicio.
     */
    private fun updateStartHour(date: Date) {
            startTimePicker.hours = DateUtils.getFromDate(date, Calendar.HOUR_OF_DAY)
            startTimePicker.minutes = DateUtils.getFromDate(date, Calendar.MINUTE)
    }

    /**
     * Actualiza el campo de texto de la hora de fin con la
     * hora de FIN pasada como parámetro.
     *
     * @param date hora de fin.
     */
    private fun updateEndHour(date: Date) {
        endTimePicker.hours = DateUtils.getFromDate(date, Calendar.HOUR_OF_DAY)
        endTimePicker.minutes = DateUtils.getFromDate(date, Calendar.MINUTE)
    }

    /**
     * Actualiza el adapter de alarmas de localización con la lista
     * pasada como parámetro. En el callback de actualización del adapter, se
     * mueve el recyclerview hasta el primer elemento de la lista.
     */
    private fun updateAlarmsAdapter(alarms: List<LocationAlarm>){
        locationAlarmsAdapter.submitList(alarms.toList()) {
            binding.recyclerViewLocationAlarms.scrollToPosition(0)
        }
    }

    /**
     * Comprueba la configuración de la localización del
     * dispositivo. Comprueba que el GPS esté activado, y
     * también que se disponga de los permisos adecuados.
     */
    private fun checkLocationSettings(){
        val dialogBuilder = AlertDialog.Builder(requireContext())
        if(!AndroidUtils.check(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)){
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

}