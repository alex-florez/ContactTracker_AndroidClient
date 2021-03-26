package es.uniovi.eii.contacttracker.fragments.dialogs.timepicker

/**
 * Listener para la selección de horas y minutos
 * en un TimePicker.
 */
interface OnTimeSetListener {
    fun onTimeSet(hour: Int, minute: Int)
}