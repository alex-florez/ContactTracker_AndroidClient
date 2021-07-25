package es.uniovi.eii.contacttracker.fragments.dialogs.personaldata


import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.DialogPersonalDataBinding
import es.uniovi.eii.contacttracker.model.PersonalData

/**
 * Diálogo modal que presenta un formulario para incluir
 * datos personales del usuario.
 */
class PersonalDataDialog(
    val listener: PersonalDataListener,
    private val defaultPersonalData: PersonalData?
) : DialogFragment() {

    /**
     * Interfaz Listener para el diálogo de datos personales.
     */
    interface PersonalDataListener {
        fun onAccept(personalData: PersonalData)
    }

    /* View Binding */
    private lateinit var binding: DialogPersonalDataBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inicializar Binding
        val inflater = requireActivity().layoutInflater
        binding = DialogPersonalDataBinding.inflate(inflater)
        binding.data = defaultPersonalData
        return initDialog(binding.root)
    }

    /**
     * Crea e inicializa el diálogo de datos personales.
     *
     * @param view Vista que contendrá el Alert Dialog.
     * @return AlertDialog.
     */
    private fun initDialog(view: View): AlertDialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)
            .setPositiveButton(R.string.accept, null)
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
        val dialog = builder.create()
        // Listener para el Botón Positivo para que no se cierre el diálogo.
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val personalData = getPersonalData()
                if(validateData(personalData)){ // Validar los datos
                    listener.onAccept(personalData)
                    dialog.dismiss()
                }
            }
        }
        return dialog
    }

    /**
     * Obtiene los datos personales introducidos a través
     * del formulario y devuelve un objeto PersonalData.
     *
     * @return objeto PersonalData con los datos del formulario.
     */
    private fun getPersonalData(): PersonalData {
        return binding.let {
            PersonalData(it.txtDNIEditText.text.toString(),
                    it.txtNameEditText.text.toString(),
                    it.txtSurnameEditText.text.toString(),
                    it.txtPhoneNumberEditText.text.toString(),
                    it.txtCityEditText.text.toString(),
                    it.txtCPEditText.text.toString())
        }
    }

    /**
     * Devuelve true si los datos personales introducidos
     * son correctos, en otro caso devuelve false y muestra
     * los errores correspondientes.
     *
     * @param data Datos personales a validar.
     */
    private fun validateData(data: PersonalData): Boolean {
        removeErrors()
        var valid = true
        val invalidInputs = mutableListOf<TextInputLayout>() // Lista de campos inválidos.
        if(data.name.isEmpty())
            invalidInputs.add(binding.txtNameLayout)
        if(data.surname.isEmpty())
            invalidInputs.add(binding.txtSurnameLayout)
        if(data.dni.isEmpty())
            invalidInputs.add(binding.txtDNILayout)
        if(data.phoneNumber.isEmpty())
            invalidInputs.add(binding.txtPhoneNumberLayout)
        if(data.city.isEmpty())
            invalidInputs.add(binding.txtCityLayout)
        if(data.cp.isEmpty())
            invalidInputs.add(binding.txtCPLayout)

        if(invalidInputs.isNotEmpty())
            valid = false
        invalidInputs.forEach {
            showError(it, getString(R.string.required_field))
        }
        return valid
    }

    /**
     * Muestra el mensaje de error pasado como parámetro en
     * el layout del campo de texto pasado como parámetro.
     *
     * @param textInput Referencia al layout del campo de texto.
     * @param error Mensaje de error.
     */
    private fun showError(textInput: TextInputLayout, error: String){
        textInput.isErrorEnabled = true
        textInput.error = error
    }

    /**
     * Elimina todos los errores de los campos
     * que haya actualmente visibles.
     */
    private fun removeErrors(){
        val textInputs = arrayListOf(
            binding.txtNameLayout, binding.txtSurnameLayout, binding.txtDNILayout,
            binding.txtPhoneNumberLayout, binding.txtCityLayout, binding.txtCPLayout)

        textInputs.forEach {
            it.error = null
            it.isErrorEnabled = false
        }
    }
}