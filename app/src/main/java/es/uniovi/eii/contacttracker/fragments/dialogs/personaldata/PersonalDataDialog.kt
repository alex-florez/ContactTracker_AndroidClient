package es.uniovi.eii.contacttracker.fragments.dialogs.personaldata


import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import es.uniovi.eii.contacttracker.R

/**
 * Diálogo modal que presenta un formulario para incluir
 * datos personales del usuario.
 */
class PersonalDataDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_personal_data, null))
                .setPositiveButton(R.string.accept, DialogInterface.OnClickListener { dialog, id ->
                }).setNegativeButton(R.string.cancel, DialogInterface.OnClickListener {dialog, id ->
                    dialog.cancel()
                })

        return builder.create()
    }
}