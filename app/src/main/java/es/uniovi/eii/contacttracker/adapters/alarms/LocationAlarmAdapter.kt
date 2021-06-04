package es.uniovi.eii.contacttracker.adapters.alarms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.ItemCardLocationAlarmBinding
import es.uniovi.eii.contacttracker.model.LocationAlarm
import es.uniovi.eii.contacttracker.util.LocationUtils
import es.uniovi.eii.contacttracker.util.Utils

/**
 * Adapter de tipo lista para las alarmas de localización.
 */
class LocationAlarmAdapter(
        private val onRemoveListener: OnRemoveAlarmClickListener,
        private val onAlarmStateChangedListener: OnAlarmStateChangedListener
) : ListAdapter<LocationAlarm, LocationAlarmAdapter.LocationAlarmViewHolder>(LocationAlarm.DIFF_CALLBACK) {

    /**
     * Listener para el botón de eliminar una
     * Alarma de localización.
     */
    interface OnRemoveAlarmClickListener {
        fun onRemove(locationAlarm: LocationAlarm)
    }

    /**
     * Interfaz Listener para el cambio de estado de
     * la alarma de localización.
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
            binding.startHour.text = Utils.formatDate(alarm.startDate, "HH:mm")
            binding.endHour.text = Utils.formatDate(alarm.endDate, "HH:mm")
            binding.switchLocationAlarm.isChecked = alarm.active
            binding.alarmDate.text = Utils.formatDate(alarm.startDate, "dd/MM/yyyy")
            // Listeners
            binding.btnRemoveAlarm.setOnClickListener{ // Eliminar alarma
                onRemoveListener.onRemove(alarm)
            }
            binding.switchLocationAlarm.setOnCheckedChangeListener { _, isChecked ->
                onAlarmStateChangedListener.onChanged(alarm, isChecked)
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


    /**
     * Se encarga de añadir una nueva alarma de localización
     * a la lista gestionada por el Adapter.
     */
    fun addAlarm(alarm: LocationAlarm) {
        val copy = currentList.toMutableList()
        copy.add(alarm)
        submitList(copy)
    }
}