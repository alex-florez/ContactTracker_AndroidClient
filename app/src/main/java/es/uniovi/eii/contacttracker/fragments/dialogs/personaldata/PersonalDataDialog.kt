package es.uniovi.eii.contacttracker.fragments.dialogs.personaldata


import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
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
     * Interfaz Listener para el diálogo
     * de datos personales.
     */
    interface PersonalDataListener {
        fun onAccept(personalData: PersonalData)
    }

    /**
     * View Binding
     */
    private lateinit var binding: DialogPersonalDataBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        binding = DialogPersonalDataBinding.inflate(inflater)
        builder.setView(binding.root)
                .setPositiveButton(R.string.accept,null)
                .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener {dialog, _ ->
                    dialog.cancel()
                })

        setPersonalData()
        val dialog = builder.create()
        // Listener para el Botón Positivo para que no se cierre el diálogo.
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                // Validar los datos
                val personalData = getPersonalData()
                if(validateData(personalData)){
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
     * Establece los datos personales por defecto pasado
     * como parámetro en los campos del diálogo.
     */
    private fun setPersonalData() {
        defaultPersonalData?.apply {
            binding.apply {
                txtDNIEditText.setText(dni)
                txtNameEditText.setText(name)
                txtSurnameEditText.setText(surname)
                txtPhoneNumberEditText.setText(phoneNumber)
                txtCityEditText.setText(city)
                txtCPEditText.setText(cp)
            }
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
        val textInputs = arrayListOf<TextInputLayout>(
            binding.txtNameLayout, binding.txtSurnameLayout, binding.txtDNILayout,
            binding.txtPhoneNumberLayout, binding.txtCityLayout, binding.txtCPLayout)

        textInputs.forEach {
            it.error = null
            it.isErrorEnabled = false
        }
    }

    /**
     * Se encarga de validar todos los datos del formulario
     * de datos personales.
     *
     * @return true si todos los datos del formulario son válidos.
     */
    private fun validatePersonalData(): Boolean {
        return false
    }
}