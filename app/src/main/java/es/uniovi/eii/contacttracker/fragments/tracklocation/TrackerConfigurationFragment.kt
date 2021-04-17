package es.uniovi.eii.contacttracker.fragments.tracklocation

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.FragmentTrackerConfigurationBinding
import es.uniovi.eii.contacttracker.util.Utils
import es.uniovi.eii.contacttracker.viewmodels.TrackerConfigurationViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [TrackerConfigurationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TrackerConfigurationFragment : Fragment() {

    /**
     * ViewModel
     */
    private val viewModel: TrackerConfigurationViewModel by viewModels()

    /**
     * ViewBinding
     */
    private lateinit var binding: FragmentTrackerConfigurationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackerConfigurationBinding.inflate(inflater, container, false)
        setObservers()
        setListeners()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Recuperar valores almacenados.
        viewModel.getMinInterval()
        viewModel.getSmallestDisplacement()
    }

    /**
     * Establece y configura los observers para los LiveData
     * del ViewModel.
     */
    private fun setObservers(){
        viewModel.apply {
            // Intervalo mínimo
            minInterval.observe(viewLifecycleOwner, {
                val minuteSeconds = viewModel.getMinIntervalMinSecs()
                if(minuteSeconds.isNotEmpty()) {
                    val minIntervalText = "${minuteSeconds[0]} min ${minuteSeconds[1]} secs"
                    binding.txtInputMinInterval.setText(minIntervalText)
                }
            })
            // Desplazamiento mínimo
            smallestDisplacement.observe(viewLifecycleOwner, {
                val metersText = "$it m"
                binding.txtInputMinDisplacement.setText(metersText)
            })
        }
    }

    /**
     * Establece los componentes Listener para los distintos
     * elementos de la UI.
     */
    private fun setListeners(){
        binding.apply {
            // Input intervalo mínimo
            txtInputMinInterval.setOnClickListener{
                createMinIntervalNumberPicker().show()
            }
            // Input desplazamiento mínimo
            txtInputMinDisplacement.setOnClickListener{
                createMinDisplacementNumberPicker().show()
            }
        }
    }

    /**
     * Crea y configura el NumberPicker para seleccionar el intervalo
     * mínimo de tiempo entre actualizaciones de localizazción.
     *
     * @return AlertDialog con el NumberPicker.
     */
    private fun createMinIntervalNumberPicker(): AlertDialog {
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.minute_seconds_number_picker, binding.root, false)
        // NumberPickers
        val minutePicker = view.findViewById<NumberPicker>(R.id.numberPickerMinutes)
        val secondsPicker = view.findViewById<NumberPicker>(R.id.numberPickerSeconds)
        val actualValue = viewModel.getMinIntervalMinSecs()
        minutePicker.minValue = 0
        minutePicker.maxValue = 60
        secondsPicker.minValue = 0
        secondsPicker.maxValue = 60

        if(actualValue.isNotEmpty()){ // Valor actual
            minutePicker.value = actualValue[0]
            secondsPicker.value = actualValue[1]
        }
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)
        // Aceptar
        builder.setPositiveButton(getString(R.string.accept)) { _, _ ->
            updateMinInterval(arrayOf(minutePicker.value, secondsPicker.value))
        }
        // Cancelar
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }

        return builder.create()
    }

    /**
     * Crea y configura el NumberPicker para seleccionar los metros mínimos
     * de desplazamiento.
     */
    private fun createMinDisplacementNumberPicker(): AlertDialog {
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.meters_number_picker, binding.root, false)
        val metersPicker = view.findViewById<NumberPicker>(R.id.numberPickerMeters)

        metersPicker.minValue = 0
        metersPicker.maxValue = Constants.MAX_DISPLACEMENT
        metersPicker.value = viewModel.smallestDisplacement.value?.toInt() ?: 0
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)
        builder.setPositiveButton(getText(R.string.accept)) { _, _ ->
            updateSmallestDisplacement(metersPicker.value)
        }
        builder.setNegativeButton(getText(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        return builder.create()
    }


    /**
     * Actualiza la configuración del intervalo mínimo
     * de tiempo almacenada en las SharedPrefs, con los minutos
     * y segundos pasados como parámetro.
     * @param minIntervalArray array con los minutos y segundos.
     */
    private fun updateMinInterval(minIntervalArray: Array<Int>) {
        if(minIntervalArray.isNotEmpty()){
            viewModel.updateMinInterval(
                    Utils.getMillis(minIntervalArray[0], minIntervalArray[1]))
        }
    }

    /**
     * Invoca al ViewModel para actualizar el parámetro del
     * desplazamiento mínimo con el nuevo valor seleccionado desde
     * el NumberPicker.
     *
     * @param meters metros de desplazamiento.
     */
    private fun updateSmallestDisplacement(meters: Int) {
        viewModel.updateSmallestDisplacement(meters.toFloat())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ThirdItemFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TrackerConfigurationFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}