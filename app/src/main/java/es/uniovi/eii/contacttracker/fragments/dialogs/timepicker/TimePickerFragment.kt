package es.uniovi.eii.contacttracker.fragments.dialogs.timepicker

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

/**
 * Fragment que presenta un diálogo al usuario para mostrar
 * un TimePricker.
 */
class TimePickerFragment(
    private val listener: OnTimeSetListener,
    private val title: String
) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireActivity(), this, hour, minute,
            DateFormat.is24HourFormat(requireActivity()))
        timePickerDialog.setTitle(title)
        return timePickerDialog
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
       listener.onTimeSet(hourOfDay, minute)
    }
}