package es.uniovi.eii.contacttracker.fragments.dialogs.personaldata


import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
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
                .setPositiveButton(R.string.accept, DialogInterface.OnClickListener { _, _ ->
                    listener.onAccept(getPersonalData())
                }).setNegativeButton(R.string.cancel, DialogInterface.OnClickListener {dialog, _ ->
                    dialog.cancel()
                })

        setPersonalData()
        return builder.create()
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
     * Se encarga de validar todos los datos del formulario
     * de datos personales.
     *
     * @return true si todos los datos del formulario son válidos.
     */
    private fun validatePersonalData(): Boolean {
        return false
    }
}