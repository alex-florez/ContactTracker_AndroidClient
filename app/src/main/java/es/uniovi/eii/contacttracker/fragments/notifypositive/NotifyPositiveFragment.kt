package es.uniovi.eii.contacttracker.fragments.notifypositive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.FragmentNotifyPositiveBinding
import es.uniovi.eii.contacttracker.fragments.dialogs.personaldata.PersonalDataConsentDialog
import es.uniovi.eii.contacttracker.fragments.dialogs.personaldata.PersonalDataDialog
import es.uniovi.eii.contacttracker.model.Error
import es.uniovi.eii.contacttracker.model.PersonalData
import es.uniovi.eii.contacttracker.util.AndroidUtils
import es.uniovi.eii.contacttracker.util.ValueWrapper
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
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setListeners()
        setObservers()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadInfectivityPeriod() // Leer el periodo de infectividad de la configuración.
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
        }
    }

    /**
     * Configura los observers para los datos
     * del View Model.
     */
    private fun setObservers(){
        viewModel.apply {
            // Periodo de infectividad
            infectivityPeriod.observe(viewLifecycleOwner) {
                binding.txtInfectivityPeriod.text = resources.getQuantityString(R.plurals.daysText, it, it)
            }
            notifyResult.observe(viewLifecycleOwner) {
                when(it){
                    is ValueWrapper.Success -> {
                        AndroidUtils.snackbar(getString(R.string.notifyPositiveResultText, it.value.uploadedLocations),
                            Snackbar.LENGTH_LONG, binding.root, requireActivity())
                    }
                    is ValueWrapper.Fail -> { processError(it.error) }
                }
            }
        }
    }

    /**
     * Se encarga de procesar los posibles errores que surjan durante
     * la notificación de un positivo.
     */
    private fun processError(error: Error) {
        when(error) {
            Error.TIMEOUT -> {
                AndroidUtils.snackbar(getString(R.string.network_error), Snackbar.LENGTH_LONG,
                    binding.root, requireActivity())
            }
            Error.CANNOT_NOTIFY -> {
                AndroidUtils.snackbar(getString(R.string.genericErrorNotifyPositive), Snackbar.LENGTH_LONG,
                    binding.root, requireActivity())
            }
            Error.NOTIFICATION_LIMIT_EXCEEDED -> {
                AndroidUtils.snackbar(getString(R.string.errorNotifyLimitExceeded), Snackbar.LENGTH_LONG,
                    binding.root, requireActivity())
            }
            Error.NO_LOCATIONS_TO_NOTIFY -> {
                AndroidUtils.snackbar(getString(R.string.errorNotifyNoLocations), Snackbar.LENGTH_LONG,
                    binding.root, requireActivity())
            }
            else -> {
                AndroidUtils.snackbar(getString(R.string.genericError), Snackbar.LENGTH_LONG,
                    binding.root, requireActivity())
            }
        }
    }

    /**
     * Método invocado cuando se pulsa sobre el botón
     * de notificar un positivo.
     */
    private fun notifyPositiveClick(){
        if(binding.checkBoxAddPersonalData.isChecked){ // Agregar datos personales
            notifyPositiveWithPersonalData()
        } else {  // Notificar sin aportar datos personales
            viewModel.notifyPositive(false)
        }
    }

    /**
     * Método Wrapper para notificar un positivo pero agregando
     * además los datos personales del usuario, mostrando los diálogos
     * correspondientes.
     */
    private fun notifyPositiveWithPersonalData(){
        // Dialog para rellenar los datos personales.
        createPersonalDataDialog(object : PersonalDataDialog.PersonalDataListener {
            override fun onAccept(personalData: PersonalData) {
                viewModel.savePersonalData(personalData) // Almacenar los datos personales
                // Dialog con la cláusula de consentimiento
                createPersonalDataAgreementDialog(
                    object : PersonalDataConsentDialog.PrivacyPolicyListener {
                        override fun onAcceptPolicy() { // Política aceptada
                           viewModel.notifyPositive(true)
                        }

                        override fun onRejectPolicy() { // Política rechazada

                        }
                    }
                ).show(requireActivity().supportFragmentManager, "Personal Data Agreement")
            }
        }).show(requireActivity().supportFragmentManager, "Personal Data")
    }

    /**
     * Construye y configura el diálogo modal que contiene un
     * formulario para agregar los datos personales del usuario
     * al positivo notificado.
     */
    private fun createPersonalDataDialog(onAccept: PersonalDataDialog.PersonalDataListener): PersonalDataDialog {
        // Crear diálogo y pasarle listener de Accept.
        return PersonalDataDialog(onAccept, viewModel.getPersonalData())
    }

    private fun createPersonalDataAgreementDialog(privacyPolicyListener: PersonalDataConsentDialog.PrivacyPolicyListener): PersonalDataConsentDialog {
        return PersonalDataConsentDialog(privacyPolicyListener)
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