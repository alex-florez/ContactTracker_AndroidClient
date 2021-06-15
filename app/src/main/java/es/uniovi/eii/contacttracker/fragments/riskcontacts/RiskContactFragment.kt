package es.uniovi.eii.contacttracker.fragments.riskcontacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.FragmentRiskContactBinding
import es.uniovi.eii.contacttracker.viewmodels.RiskContactViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * Enumerado para el modo de comprobación.
 */
enum class CHECK_MODE {
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
     * Modo de comprobación seleccionado.
     */
    private var checkMode = CHECK_MODE.MANUAL


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        selectMode(CHECK_MODE.MANUAL) // Leer el modo de comprobación de las SharedPrefs
    }

    /**
     * Establece los listeners de eventos para los componentes
     * de la UI.
     */
    private fun setListeners(){
        binding.apply {
            radioBtnManualCheck.setOnClickListener {
                selectMode(CHECK_MODE.MANUAL)
            }

            radioBtnPeriodicCheck.setOnClickListener {
                selectMode(CHECK_MODE.PERIODIC)
            }
        }
    }

    /**
     * Selecciona el modo de comprobación en función
     * del RadioButton que se haya pulsado.
     *
     * @param newCheckMode Nuevo modo de comprobación.
     */
    private fun selectMode(newCheckMode: CHECK_MODE){
        this.checkMode = newCheckMode
        // Gestionar estado de los radio buttons
        when(newCheckMode){
            CHECK_MODE.MANUAL -> {
                toggleManualCheck(true)
                togglePeriodicCheck(false)
            }
            CHECK_MODE.PERIODIC -> {
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