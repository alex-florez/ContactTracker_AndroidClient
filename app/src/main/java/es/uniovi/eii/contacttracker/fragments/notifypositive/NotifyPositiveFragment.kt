package es.uniovi.eii.contacttracker.fragments.notifypositive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.FragmentNotifyPositiveBinding
import es.uniovi.eii.contacttracker.fragments.dialogs.notifyquestions.NotifyQuestionsDialog
import es.uniovi.eii.contacttracker.fragments.dialogs.personaldata.PersonalDataConsentDialog
import es.uniovi.eii.contacttracker.fragments.dialogs.personaldata.PersonalDataDialog
import es.uniovi.eii.contacttracker.model.PersonalData
import es.uniovi.eii.contacttracker.util.AndroidUtils
import es.uniovi.eii.contacttracker.viewmodels.NotifyPositiveViewModel
import java.util.Date


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
                onNotifyPositive()
            }
            // Botón para consultar la política de privacidad
            btnPrivacyPolicy.setOnClickListener {
                showPrivacyPolicy()
            }
        }
    }

    /**
     * Configura los observers para los LiveData del View Model.
     */
    private fun setObservers(){
        viewModel.apply {
            // Periodo de infectividad
            infectivityPeriod.observe(viewLifecycleOwner) {
                binding.txtInfectivityPeriod.text = resources.getQuantityString(R.plurals.daysText, it, it)
            }
            // Resultado de notificar un positivo
            notifySuccess.observe(viewLifecycleOwner) {
                AndroidUtils.snackbar(getString(it.first, it.second),
                    Snackbar.LENGTH_LONG, binding.root, requireActivity())
            }
            notifyError.observe(viewLifecycleOwner) {
                AndroidUtils.snackbar(getString(it.first, it.second), Snackbar.LENGTH_LONG,
                    binding.root, requireActivity())
            }
        }
    }

    /**
     * Método invocado cuando se pulsa sobre el botón
     * de notificar un positivo.
     */
    private fun onNotifyPositive(){
        if(binding.checkBoxAddPersonalData.isChecked){ // Agregar datos personales
            notifyPositiveWithPersonalData()
        } else {  // Notificar sin aportar datos personales
           notifyPositive(false)
        }
    }

    /**
     * Invoca al método de ViewModel para notificar el positivo. Recibe como
     * parámetro un flag que indica si se van a incluir los datos personales.
     *
     * @param addPersonalData Indica si se van a incluir los datos personales.
     */
    private fun notifyPositive(addPersonalData: Boolean) {
        // Crear el diálogo con las preguntas.
        createNotifyQuestionsDialog(object : NotifyQuestionsDialog.QuestionsListener {
            override fun onAccept(answers: Map<String, Boolean>) {
                // Pasarle las respuestas a las preguntas.
                viewModel.notifyPositive(addPersonalData, answers, Date())
            }
        }).show(requireActivity().supportFragmentManager, "Notify Questions")
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
                          notifyPositive(true)
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

    /**
     * Construye el diálogo modal que contiene la cláusula de consentimiento
     * para el tratamiento de los datos personales del usuario.
     */
    private fun createPersonalDataAgreementDialog(privacyPolicyListener: PersonalDataConsentDialog.PrivacyPolicyListener): PersonalDataConsentDialog {
        return PersonalDataConsentDialog(privacyPolicyListener)
    }

    /**
     * Crea e inicializa el diálogo modal que contiene las preguntas para el usuario
     * acerca de si ha tenido síntomas y si está vacunado.
     */
    private fun createNotifyQuestionsDialog(questionsListener: NotifyQuestionsDialog.QuestionsListener): NotifyQuestionsDialog {
        return NotifyQuestionsDialog(questionsListener)
    }

    /**
     * Navega hasta el fragmento que contiene la Política de Privacidad de la App.
     */
    private fun showPrivacyPolicy() {
        val action = NotifyPositiveFragmentDirections.showPrivacyPolicy()
        findNavController().navigate(action)
    }
}