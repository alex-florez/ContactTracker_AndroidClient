package es.uniovi.eii.contacttracker.adapters.locations

import androidx.recyclerview.widget.DiffUtil
import es.uniovi.eii.contacttracker.model.UserLocation

/**
 * Clase que implementa el Callback para comprobar diferencias
 * entre dos objetos UserLocation.
 */
class UserLocationDiffCallback : DiffUtil.ItemCallback<UserLocation>() {
    override fun areItemsTheSame(oldItem: UserLocation, newItem: UserLocation): Boolean {
        if(oldItem.userlocationID == null || newItem.userlocationID == null){
            return false
        }
        return oldItem.userlocationID == newItem.userlocationID
    }

    override fun areContentsTheSame(oldItem: UserLocation, newItem: UserLocation): Boolean {
       return oldItem == newItem // Igualdad por contenido
    }
}