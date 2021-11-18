package es.uniovi.eii.contacttracker.fragments.dialogs.notifyquestions

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.DialogNotifyQuestionsBinding

/* Clave para la pregunta de si el usuario es asintomático */
const val ASYMPTOMATIC_QUESTION = "Asymptomatic"
/* Clave para la pregunta de si el usuario está vacunado */
const val VACCINATED_QUESTION = "Vaccinated"

/**
 * Diálogo que contiene las preguntas a contestar por el usuario
 * cuando este notifica un nuevo positivo.
 */
class NotifyQuestionsDialog(
    val listener: QuestionsListener
) : DialogFragment() {

    /**
     * Interfaz Listener para el diálogo con las preguntas
     * a contestar por el usuario.
     */
    interface QuestionsListener {
        fun onAccept(answers: Map<String, Boolean>)
    }

    /* View Binding */
    private lateinit var binding: DialogNotifyQuestionsBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        binding = DialogNotifyQuestionsBinding.inflate(inflater)
        return initDialog(binding.root)
    }

    /**
     * Crea e inicializa un diálogo de alerta con la vista pasada como parámetro.
     *
     * @param view Vista raíz del layout del diálogo.
     * @return Diálogo de alerta.
     */
    private fun initDialog(view: View): AlertDialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)
            .setPositiveButton(R.string.accept) { dialog, _ ->
                listener.onAccept(getAnswers())
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }

        return builder.create()
    }

    /**
     * Devuelve un mapa de pares clave valor con la pregunta y el
     * valor booleano asociado (sí o no) a partir del estado de
     * los chips de la interfaz de usuario.
     *
     * @return Mapa con pares clave String valor Boolean para cada pregunta.
     */
    private fun getAnswers(): Map<String, Boolean> {
        val selectedAnswer1 = binding.question1.checkedChipId
        val selectedAnswer2 = binding.question2.checkedChipId
        val a1 = when(selectedAnswer1) {
            R.id.q1Yes -> false
            R.id.q1No -> true
            else -> false
        }
        val a2 = when(selectedAnswer2) {
            R.id.q2Yes -> true
            R.id.q2No -> false
            else -> false
        }
        return mapOf(
            ASYMPTOMATIC_QUESTION to a1,
            VACCINATED_QUESTION to a2
        )
    }
}