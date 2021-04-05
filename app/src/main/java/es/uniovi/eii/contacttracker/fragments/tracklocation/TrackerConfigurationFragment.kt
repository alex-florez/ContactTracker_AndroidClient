package es.uniovi.eii.contacttracker.fragments.tracklocation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.FragmentTrackerConfigurationBinding
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

        viewModel.getMinInterval()
    }

    /**
     * Establece y configura los observers para los LiveData
     * del ViewModel.
     */
    private fun setObservers(){
        viewModel.minInterval.observe(viewLifecycleOwner, {
            val seconds = "${it/1000} s"
            binding.txtInputMinInterval.setText(seconds)
        })

    }

    /**
     * Establece los componentes Listener para los distintos
     * elementos de la UI.
     */
    private fun setListeners(){
        binding.btnApplyTrackerConfig.setOnClickListener{
            applyConfig()
        }
    }

    /**
     * Método invocado cuando se pulsa sobre el Botón
     * para aplicar los cambios en la configuración del tracker.
     */
    private fun applyConfig(){
        val seconds = binding.txtInputMinInterval.text.toString().toInt()
        viewModel.updateMinInterval(seconds)
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