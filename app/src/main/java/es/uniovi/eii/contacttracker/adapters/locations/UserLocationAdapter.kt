package es.uniovi.eii.contacttracker.adapters.locations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.ItemCardUserLocationBinding
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.util.DateUtils
import es.uniovi.eii.contacttracker.util.NumberUtils.round

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

/* Instancia del DIFF CALLBACK */
private val DIFF_CALLBACK = UserLocationDiffCallback()

/**
 * Adapter para los objetos UserLocation que almacenan
 * información sobre la localización del usuario.
 */
class UserLocationAdapter(
   private val onClickListener: OnUserLocationItemClick
): ListAdapter<UserLocation, UserLocationAdapter.UserLocationViewHolder>(DIFF_CALLBACK){

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
            val formattedDate = DateUtils.formatDate(location.timestamp(), "dd/MM/yyyy")
            val formattedHour = DateUtils.formatDate(location.timestamp(), "HH:mm:ss")
            binding.apply {
                txtLocationLat.text = round(location.lat(), 6).toString()
                txtLocationLng.text = round(location.lng(), 6).toString()
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