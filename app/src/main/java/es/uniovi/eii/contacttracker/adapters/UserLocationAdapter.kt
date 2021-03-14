package es.uniovi.eii.contacttracker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.ItemUserLocationBinding
import es.uniovi.eii.contacttracker.model.UserLocation
import java.text.SimpleDateFormat

/**
 * Adapter para los objetos UserLocation que almacenan
 * información sobre la localización del usuario.
 */
class UserLocationAdapter :
    ListAdapter<UserLocation, UserLocationAdapter.UserLocationViewHolder>(UserLocation.DIFF_CALLBACK){


    /**
     * ViewHolder para los objetos UserLocation.
     */
    class UserLocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * ViewBinding
         */
        private val binding = ItemUserLocationBinding.bind(itemView)

        /**
         * Se encarga de enlazar el objeto UserLocation con
         * los componentes de la vista del ViewHolder.
         */
        fun bindUserLocation(location: UserLocation){
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            binding.txtLocationLat.text = "Lat: ${location.lat}"
            binding.txtLocationLng.text = "Lng: ${location.lng}"
            binding.txtLocationAcc.text = "Acc: ${location.accuracy}"
            binding.txtLocationProvider.text = "Provider: $${location.provider}"
            binding.txtLocationTimestamp.text = dateFormatter.format(location.timestamp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserLocationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_location, parent, false)
        return UserLocationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserLocationViewHolder, position: Int) {
       holder.bindUserLocation(getItem(position))
    }
}