package es.uniovi.eii.contacttracker.fragments.riskcontacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.databinding.FragmentRiskContactBinding
import es.uniovi.eii.contacttracker.fragments.dialogs.timepicker.OnTimeSetListener
import es.uniovi.eii.contacttracker.fragments.dialogs.timepicker.TimePickerFragment
import es.uniovi.eii.contacttracker.util.Utils
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

    /**
     * Modo de comprobación seleccionado.
     */
    private var checkMode = CheckMode.MANUAL


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createTimePicker()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentRiskContactBinding.inflate(inflater, container, false)

        binding.btnManualCheck.setOnClickListener {
            viewModel.detect()
        }
        viewModel.isDetecting.observe(viewLifecycleOwner) {
            binding.btnManualCheck.isEnabled = !it
        }
        setListeners()
        setObservers()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        selectMode(viewModel.getCheckMode()) // Leer el modo de comprobación de las SharedPrefs
        updateCheckHour(viewModel.getCheckHour()) // Leer la hora de la comprobación de las SharedPrefs
    }

    /**
     * Establece los listeners de eventos para los componentes
     * de la UI.
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

            /* EditText para la hora de la comprobación */
            txtCheckHour.setOnClickListener {
                checkHourTimePicker.show(requireActivity().supportFragmentManager, "CheckHour")
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
                updateCheckHour(it)
            }
        }
    }

    /**
     * Actualiza el campo de texto con la nueva hora de comprobación,
     * además de actualizar las horas y minutos del TimePicker.
     */
    private fun updateCheckHour(date: Date) {
        val checkHourText = Utils.formatDate(date, "HH:mm")
        binding.txtCheckHour.setText(checkHourText)
        checkHourTimePicker.hours = Utils.getFromDate(date, Calendar.HOUR_OF_DAY)
        checkHourTimePicker.minutes = Utils.getFromDate(date, Calendar.MINUTE)
    }

    /**
     * Instancia el Time Picker para seleccionar la hora
     * de comprobación.
     */
    private fun createTimePicker(){
        this.checkHourTimePicker = TimePickerFragment(object : OnTimeSetListener {
            override fun onTimeSet(hour: Int, minute: Int) {
                viewModel.setCheckHour(Utils.getDate(hour, minute))
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
        this.checkMode = newCheckMode
        viewModel.setCheckMode(newCheckMode) // Guardar el modo de comprobación en las SharedPrefs
        // Gestionar estado de los radio buttons
        when(newCheckMode){
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