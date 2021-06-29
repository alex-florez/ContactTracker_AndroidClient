package es.uniovi.eii.contacttracker.adapters.locations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.ItemCardUserLocationBinding
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.util.Utils
import java.text.SimpleDateFormat

/**
 * Adapter para los objetos UserLocation que almacenan
 * información sobre la localización del usuario.
 */
class UserLocationAdapter(
   private val onClickListener: OnUserLocationItemClick
): ListAdapter<UserLocation, UserLocationAdapter.UserLocationViewHolder>(UserLocation.DIFF_CALLBACK){

    /**
     * Declaración interna de la interfaz para los eventos
     * de click de los items del adapter.
     */
    interface OnUserLocationItemClick {
        fun onClick(userLocation: UserLocation)
    }

    /**
     * ViewHolder para los objetos UserLocation.
     */
    class UserLocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * ViewBinding
         */
        private val binding = ItemCardUserLocationBinding.bind(itemView)

        /**
         * Se encarga de enlazar el objeto UserLocation con
         * los componentes de la vista del ViewHolder.
         */
        fun bindUserLocation(location: UserLocation, onClickListener: OnUserLocationItemClick){
            val formattedDate = Utils.formatDate(location.locationTimestamp, "dd/MM/yyyy")
            val formattedHour = Utils.formatDate(location.locationTimestamp, "HH:mm:ss")
            binding.apply {
                txtLocationLat.text = Utils.round(location.lat, 6).toString()
                txtLocationLng.text = Utils.round(location.lng, 6).toString()
                txtLocationAccuracy.text = location.accuracy.toString()
                txtLocationDate.text = formattedDate
                txtLocationHour.text = formattedHour
            }
            itemView.setOnClickListener{
                onClickListener.onClick(location)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserLocationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_user_location, parent, false)
        return UserLocationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserLocationViewHolder, position: Int) {
       holder.bindUserLocation(getItem(position), onClickListener)
    }

    /**
     * Método que añade una nueva localización de usuario
     * en la lista interna del adapter.
     *
     * @param location Localización del usuario a insertar.
     * @param callback Callback a invocar una vez se inserta la localización.
     */
    fun addUserLocation(location: UserLocation, callback: () -> Unit){
        val actual = currentList.toMutableList()
        actual.add(0, location)
        submitList(actual) {
            callback()
        }
    }
    /**
     * Cuando es invocado, limpia toda
     * la lista de localizaciones de Usuario.
     */
    fun clearLocations(){
        submitList(null)
    }

    /**
     * Devuelve true si existen localizaciones en la
     * lista.
     */
    fun areLocationsAvailable(): Boolean {
        return currentList.isNotEmpty()
    }

}