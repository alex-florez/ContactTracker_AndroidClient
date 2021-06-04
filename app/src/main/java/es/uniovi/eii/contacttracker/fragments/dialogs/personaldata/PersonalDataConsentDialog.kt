package es.uniovi.eii.contacttracker.fragments.dialogs.personaldata

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import es.uniovi.eii.contacttracker.databinding.DialogPrivacyPolicyBinding
import es.uniovi.eii.contacttracker.databinding.FragmentHistoryBinding

/**
 * Diálogo de Política de Privacidad relativo a la protección
 * de datos personales del usuario. Permite aceptar o rechazar
 * la política de privacidad.
 */
class PersonalDataConsentDialog(
        val privacyPolicyListener: PrivacyPolicyListener
) : DialogFragment(){

    /**
     * Interfaz listener
     */
    interface PrivacyPolicyListener {

        /**
         * Callaback de Aceptación de política.
         */
        fun onAcceptPolicy()

        /**
         * Callback de Rechazo de política.
         */
        fun onRejectPolicy()
    }

    /**
     * View Binding
     */
    private lateinit var binding: DialogPrivacyPolicyBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        binding = DialogPrivacyPolicyBinding.inflate(inflater)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)

        return builder.create()
    }


}