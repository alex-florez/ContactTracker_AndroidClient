package es.uniovi.eii.contacttracker.adapters.alarms

import androidx.recyclerview.widget.DiffUtil
import es.uniovi.eii.contacttracker.model.LocationAlarm

/**
 * Clase que implementa un callback para comprobar diferencias
 * entre alarmas de localización, para actualizar el adapter de
 * alarmas.
 */
class LocationAlarmDiffCallback : DiffUtil.ItemCallback<LocationAlarm>() {

    override fun areItemsTheSame(oldItem: LocationAlarm, newItem: LocationAlarm): Boolean {
        return oldItem === newItem // Igualdad por referencia
    }

    override fun areContentsTheSame(oldItem: LocationAlarm, newItem: LocationAlarm): Boolean {
        return oldItem == newItem // Igualdad por contenido
    }
}