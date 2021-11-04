package es.uniovi.eii.contacttracker.adapters.alarms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.ItemCardLocationAlarmBinding
import es.uniovi.eii.contacttracker.location.alarms.LocationAlarm
import es.uniovi.eii.contacttracker.util.DateUtils

/**
 * Clase que implementa un callback para comprobar diferencias
 * entre alarmas de localización, para actualizar el adapter de
 * alarmas.
 */
class LocationAlarmDiffCallback : DiffUtil.ItemCallback<LocationAlarm>() {

    override fun areItemsTheSame(oldItem: LocationAlarm, newItem: LocationAlarm): Boolean {
        if(oldItem.id == null || newItem.id == null)
            return false
        return oldItem.id == newItem.id // Igualdad por id
    }

    override fun areContentsTheSame(oldItem: LocationAlarm, newItem: LocationAlarm): Boolean {
        return oldItem == newItem // Igualdad por contenido
    }
}

/* Instancia del DIFF Callback */
private val DIFF_CALLBACK = LocationAlarmDiffCallback()

/**
 * Adapter de lista para las alarmas de localización.
 */
class LocationAlarmAdapter(
        private val onRemoveListener: OnRemoveAlarmClickListener,
        private val onAlarmStateChangedListener: OnAlarmStateChangedListener
) : ListAdapter<LocationAlarm, LocationAlarmAdapter.LocationAlarmViewHolder>(DIFF_CALLBACK) {

    /**
     * Interfaz Listener para eliminar una Alarma de localización.
     */
    interface OnRemoveAlarmClickListener {
        fun onRemove(locationAlarm: LocationAlarm)
    }

    /**
     * Interfaz Listener para el cambio de estado de la alarma de localización.
     */
    interface OnAlarmStateChangedListener {
        fun onChanged(locationAlarm: LocationAlarm, isChecked: Boolean)
    }

    /**
     * View Holder para los objetos de alarmas de localización.
     */
    class LocationAlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         * ViewBinding
         */
        private val binding = ItemCardLocationAlarmBinding.bind(itemView)

        /**
         * Enlaza el objeto de alarma de localización, con
         * los componentes de la vista.
         */
        fun bindLocationAlarm(alarm: LocationAlarm,
                              onRemoveListener: OnRemoveAlarmClickListener,
                              onAlarmStateChangedListener: OnAlarmStateChangedListener
        ) {
            // Vincular datos con el XML
            binding.alarm = alarm
            binding.du = DateUtils

            // Deshabilitar el listener del Switch para evitar que se disparen eventos
            binding.switchLocationAlarm.setOnCheckedChangeListener(null)
            binding.switchLocationAlarm.isChecked = alarm.active

            // Listeners
            binding.btnRemoveAlarm.setOnClickListener{
                onRemoveListener.onRemove(alarm) // Eliminar alarma
            }
            binding.switchLocationAlarm.setOnCheckedChangeListener { _, isChecked ->
                onAlarmStateChangedListener.onChanged(alarm, isChecked) // Alternar estado de la alarma
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationAlarmViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_card_location_alarm, parent, false)
        return LocationAlarmViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LocationAlarmViewHolder, position: Int) {
        holder.bindLocationAlarm(getItem(position), onRemoveListener, onAlarmStateChangedListener)
    }

    override fun getItemId(position: Int): Long {
        val alarm = getItem(position)
        return alarm.id ?: 0
    }
}