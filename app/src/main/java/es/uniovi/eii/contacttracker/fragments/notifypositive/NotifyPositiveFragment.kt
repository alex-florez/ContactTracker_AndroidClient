package es.uniovi.eii.contacttracker.fragments.notifypositive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.FragmentNotifyPositiveBinding
import es.uniovi.eii.contacttracker.viewmodels.NotifyPositiveViewModel


/**
 * Fragment para Notificar un positivo en COVID-19.
 *
 * Contiene la funcionalidad para registrar un positivo y
 * subir las localizaciones a la nube.
 */
@AndroidEntryPoint
class NotifyPositiveFragment : Fragment() {

    /**
     * View Binding
     */
    private lateinit var binding: FragmentNotifyPositiveBinding

    /**
     * View Model
     */
    private val viewModel: NotifyPositiveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentNotifyPositiveBinding.inflate(inflater, container, false)

        setListeners()
        setObservers()
        return binding.root
    }

    /**
     * Configura los listeners para los componentes
     * de la UI.
     */
    private fun setListeners(){
        binding.apply {
            btnGET.setOnClickListener{
                viewModel.getPositive()
            }
        }
    }

    /**
     * Configura los observers para los datos
     * del View Model.
     */
    private fun setObservers(){
        viewModel.apply {
            positive.observe(viewLifecycleOwner, {
                binding.txtGET.text = it.toString()
            })
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotifyPositiveFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                NotifyPositiveFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}