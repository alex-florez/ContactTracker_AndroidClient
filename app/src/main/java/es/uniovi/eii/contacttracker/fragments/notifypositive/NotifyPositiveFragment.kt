package es.uniovi.eii.contacttracker.fragments.notifypositive

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.FragmentNotifyPositiveBinding
import es.uniovi.eii.contacttracker.fragments.dialogs.personaldata.PersonalDataDialog
import es.uniovi.eii.contacttracker.model.PersonalData
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
            // Botón de notificar un positivo
            btnNotifyPositive.setOnClickListener{
                notifyPositiveClick()
            }
            // Checkbox para adjuntar los datos personales
            checkBoxAddPersonalData.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setAddPersonalData(isChecked)
            }
        }
    }

    /**
     * Configura los observers para los datos
     * del View Model.
     */
    private fun setObservers(){
        viewModel.apply {
            // Error de RED
            networkError.observe(viewLifecycleOwner, {
                Snackbar.make(binding.root, getString(R.string.network_error), Snackbar.LENGTH_LONG).let { s ->
                    s.anchorView = requireActivity().findViewById(R.id.bottomNavigationView)
                    s.show()
                }
                binding.notifyPositiveProgress.visibility = View.GONE
            })

            // Error GENÉRICO al notificar positivo
            notifyError.observe(viewLifecycleOwner, {
                Snackbar.make(binding.root, getString(R.string.genericErrorNotifyPositive), Snackbar.LENGTH_LONG).let { s ->
                    s.anchorView = requireActivity().findViewById(R.id.bottomNavigationView)
                    s.show()
                }
                binding.notifyPositiveProgress.visibility = View.GONE
            })

            // Resultado de NOTIFICAR POSITIVO
            notifyPositiveResult.observe(viewLifecycleOwner, {
                binding.notifyPositiveProgress.visibility = View.GONE
                if(it != null){
                    Snackbar.make(binding.root, "Se han subido ${it.uploadedLocations} localizaciones a la nube.", Snackbar.LENGTH_LONG).let { s ->
                        s.anchorView = requireActivity().findViewById(R.id.bottomNavigationView)
                        s.show()
                    }
                }
            })
        }
    }

    /**
     * Método invocado cuando se pulsa sobre el botón
     * de notificar un positivo.
     */
    private fun notifyPositiveClick(){
        viewModel.flagAddPersonalData.value?.let { addPersonalData ->
            if(addPersonalData){ // Agregar datos personales
                createPersonalDataDialog().show(requireActivity().supportFragmentManager, "Personal Data")
            } else { // Notificar sin aportar datos personales
                notifyPositive()
            }
        }
    }

    /**
     * Se encarga de invocar al viewModel para notificar
     * un positivo y hacer visible la barra de progreso.
     */
    private fun notifyPositive() {
        viewModel.notifyPositive()
        binding.notifyPositiveProgress.visibility = View.VISIBLE
    }
    

    /**
     * Construye y configura el diálogo modal que contiene un
     * formulario para agregar los datos personales del usuario
     * al positivo notificado.
     */
    private fun createPersonalDataDialog(): PersonalDataDialog {
        // Crear diálogo y pasarle listener de Accept.
        return PersonalDataDialog(object : PersonalDataDialog.PersonalDataListener {
            override fun onAccept(personalData: PersonalData) {
                viewModel.personalData.value = personalData // Añadir los datos personales
                notifyPositive() // Notificar positivo
            }
        })
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