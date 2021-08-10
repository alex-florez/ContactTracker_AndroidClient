package es.uniovi.eii.contacttracker.fragments.riskcontacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.children
import androidx.core.view.isEmpty
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.FragmentRiskContactBinding
import es.uniovi.eii.contacttracker.fragments.dialogs.timepicker.OnTimeSetListener
import es.uniovi.eii.contacttracker.fragments.dialogs.timepicker.TimePickerFragment
import es.uniovi.eii.contacttracker.model.Error
import es.uniovi.eii.contacttracker.riskcontact.alarms.MAX_ALARM_COUNT
import es.uniovi.eii.contacttracker.riskcontact.alarms.RiskContactAlarm
import es.uniovi.eii.contacttracker.util.AndroidUtils
import es.uniovi.eii.contacttracker.util.DateUtils
import es.uniovi.eii.contacttracker.util.ValueWrapper
import es.uniovi.eii.contacttracker.viewmodels.RiskContactViewModel
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * Enumerado para el modo de comprobación.
 */
enum class CheckMode {
    MANUAL, PERIODIC
}

/**
 * Fragmento para las comprobaciones de Contactos de Riesgo.
 */
@AndroidEntryPoint
class RiskContactFragment : Fragment() {

    /**
     * ViewBinding
     */
    private lateinit var binding: FragmentRiskContactBinding

    /**
     * ViewModel
     */
    private val viewModel: RiskContactViewModel by viewModels()

    /**
     * Time Picker para seleccionar una hora.
     */
    private lateinit var checkHourTimePicker: TimePickerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createTimePicker()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentRiskContactBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.du = DateUtils
        binding.viewModel = viewModel

        setListeners()
        setObservers()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateUI(viewModel.getCheckMode()) // Leer el modo de comprobación de las SharedPrefs
        viewModel.loadAlarms() // Cargar alarmas de comprobación actuales
    }

    /**
     * Establece los listeners de eventos para los componentes de la UI.
     */
    private fun setListeners(){
        binding.apply {
            /* Radio Buttons */
            radioBtnManualCheck.setOnClickListener {
                selectMode(CheckMode.MANUAL)
            }

            radioBtnPeriodicCheck.setOnClickListener {
                selectMode(CheckMode.PERIODIC)
            }

            /* Botón de Comprobación manual*/
            btnManualCheck.setOnClickListener {
                this@RiskContactFragment.viewModel.startChecking()
            }

            /* EditText para la hora de la comprobación */
            txtCheckHour.setOnClickListener {
                checkHourTimePicker.show(requireActivity().supportFragmentManager, "CheckHour")
            }

            /* Botón para añadir una alarma de comprobación */
            btnApplyCheckHour.setOnClickListener {
                addNewAlarm()
            }
        }
    }

    /**
     * Configura los observers para los LiveData definidos
     * en el ViewModel.
     */
    private fun setObservers(){
        viewModel.apply {
            /* Hora de la comprobación */
            checkHour.observe(viewLifecycleOwner) {
                updateTimePicker(it)
            }

            /* Resultado de añadir una alarma de comprobación */
            addAlarmResult.observe(viewLifecycleOwner) {
                when(it) {
                    is ValueWrapper.Success -> {
                        // Crear un nuevo chip
                        binding.alarmChipGroup.addView(createAlarmChip(it.value))
                        viewModel.setLabelNoAlarms(binding.alarmChipGroup.isEmpty())
                        AndroidUtils.snackbar(getString(R.string.checkAlarmSetSnackbar), Snackbar.LENGTH_LONG,
                            binding.root, requireActivity())
                    }
                    is ValueWrapper.Fail -> {
                        processError(it.error)
                    }
                }
            }

            /* Listado de alarmas de comprobación */
            alarms.observe(viewLifecycleOwner) {
                addAlarmsToChipGroup(it)
            }
        }
    }

    /**
     * Procesa el error determinado pasado como parámetro.
     *
     * @param error Error de algún tipo relacionado con la comprobación de contactos.
     */
    private fun processError(error: Error) {
        when(error) {
            Error.RISK_CONTACT_ALARM_COLLISION -> {
                AndroidUtils.snackbar(getString(R.string.checkAlarmErrorCollision), Snackbar.LENGTH_LONG,
                    binding.root, requireActivity())
            }
            Error.RISK_CONTACT_ALARM_COUNT_LIMIT_EXCEEDED -> {
                AndroidUtils.snackbar(getString(R.string.checkAlarmErrorCountLimit, MAX_ALARM_COUNT), Snackbar.LENGTH_LONG,
                    binding.root, requireActivity())
            }
            else -> {
                AndroidUtils.snackbar(getString(R.string.genericError), Snackbar.LENGTH_LONG,
                    binding.root, requireActivity())
            }
        }
    }

    /**
     * Añade una nueva alarma de comprobación con la hora seleccionada
     * en el campo de texto.
     */
    private fun addNewAlarm() {
        viewModel.checkHour.value?.let { date ->
            viewModel.addAlarm(date)
        }
    }


    /**
     * Crea un chip que representa la alarma de comprobación pasada como parámetro.
     *
     * @param alarm Nueva alarma de comprobación.
     * @return Componente Chip configurado.
     */
    private fun createAlarmChip(alarm: RiskContactAlarm): Chip? {
        alarm.id?.let { alarmID ->
            // Texto con la hora
            val chip = layoutInflater.inflate(R.layout.alarm_chip_layout, binding.alarmChipGroup, false) as Chip
            chip.id = alarmID.toInt()
            chip.text = DateUtils.formatDate(alarm.startDate, "HH:mm")
            chip.setChipBackgroundColorResource(R.color.orange)
            chip.setOnCloseIconClickListener { // Callback para el botón de eliminar
                binding.alarmChipGroup.removeView(it)
                viewModel.removeAlarm(alarmID)
                viewModel.setLabelNoAlarms(binding.alarmChipGroup.isEmpty())
            }
            return chip
        }
        return null
    }

    /**
     * Actualiza el TimePicker con las horas y minutos seleccionados
     * desde el campo de texto de la Hora de la Comprobación.
     *
     * @param date Fecha seleccionada mediante el TimePicker.
     */
    private fun updateTimePicker(date: Date) {
        checkHourTimePicker.hours = DateUtils.getFromDate(date, Calendar.HOUR_OF_DAY)
        checkHourTimePicker.minutes = DateUtils.getFromDate(date, Calendar.MINUTE)
    }

    /**
     * Instancia el Time Picker para seleccionar la hora
     * de comprobación.
     */
    private fun createTimePicker(){
        this.checkHourTimePicker = TimePickerFragment(object : OnTimeSetListener {
            override fun onTimeSet(hour: Int, minute: Int) {
                viewModel.setCheckHour(DateUtils.getDate(hour, minute))
            }
        }, "Hora de la comprobación")
    }

    /**
     * Selecciona el modo de comprobación en función
     * del RadioButton que se haya pulsado.
     *
     * @param newCheckMode Nuevo modo de comprobación.
     */
    private fun selectMode(newCheckMode: CheckMode){
        viewModel.setCheckMode(newCheckMode) // Guardar el modo de comprobación en las SharedPrefs
        // Gestionar estado de los radio buttons
        updateUI(newCheckMode)
        // Activar/Desactivar alarmas de comprobación
        when(newCheckMode) {
            CheckMode.MANUAL -> { viewModel.toggleCheckAlarms(false) }
            CheckMode.PERIODIC -> { viewModel.toggleCheckAlarms(true) }
        }
    }

    /**
     * Actualiza los componentes de la interfaz de usuario en función
     * del modo de comprobación seleccionado.
     *
     * @param checkMode Modo de comprobación.
     */
    private fun updateUI(checkMode: CheckMode) {
        when(checkMode){
            CheckMode.MANUAL -> {
                toggleManualCheck(true)
                togglePeriodicCheck(false)
            }
            CheckMode.PERIODIC -> {
                togglePeriodicCheck(true)
                toggleManualCheck(false)
            }
        }
    }

    /**
     * Muestra para cada alarma de comprobación pasada en la lista como parámetro,
     * un Chip con la hora de la comprobación que será almacenado en el ChipGroup.
     *
     * @param alarms Alarmas de comprobación.
     */
    private fun addAlarmsToChipGroup(alarms: List<RiskContactAlarm>) {
        binding.alarmChipGroup.removeAllViews()
        alarms.forEach { alarm ->
            val chip = createAlarmChip(alarm)
            // Deshabilitar el chip si se está en el modo manual
            if(chip != null && viewModel.getCheckMode() == CheckMode.MANUAL){
                chip.isEnabled = false
                chip.isCloseIconVisible = false
            }

            binding.alarmChipGroup.addView(chip)
        }
    }

    /**
     * Habilita/Deshabilita el modo manual, cambiando el estado
     * de los componentes de la UI.
     */
    private fun toggleManualCheck(isEnabled: Boolean){
        binding.apply {
            radioBtnManualCheck.isChecked = isEnabled
            txtViewManual.isEnabled = isEnabled
            btnManualCheck.isEnabled = isEnabled
        }
    }

    /**
     * Habilita/Deshabilita el modo automático, cambiando el estado
     * de los componentes de la UI.
     */
    private fun togglePeriodicCheck(isEnabled: Boolean){
        binding.apply {
            radioBtnPeriodicCheck.isChecked = isEnabled
            txtViewPeriodic.isEnabled = isEnabled
            txtInputLayoutCheckHour.isEnabled = isEnabled
            btnApplyCheckHour.isEnabled = isEnabled
            labelScheduledAlarms.isEnabled = isEnabled
            labelNoAlarms.isEnabled = isEnabled
            alarmChipGroup.children.forEach {
                val chip = it as Chip
                chip.isEnabled = isEnabled
                chip.isCloseIconVisible = isEnabled
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RiskContactFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                RiskContactFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}