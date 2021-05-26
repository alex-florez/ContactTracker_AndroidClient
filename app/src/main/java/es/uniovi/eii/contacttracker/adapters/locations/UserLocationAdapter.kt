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
     * Lista auxiliar de localizaciones. La última localización
     * está en el comienzo de la lista, de forma que se muestre la primera.
     */
    private val locations = arrayListOf<UserLocation>()


    /**
     * Referencia al recycler view al que está vinculado este adapter.
     */
    var recyclerView: RecyclerView? = null

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
                txtLocationLat.text = location.lat.toString()
                txtLocationLng.text = location.lng.toString()
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
     */
    fun addUserLocation(location: UserLocation){
        locations.add(0, location) // Añadir al principio
        submitList(locations.toList())
        // Hacer Scroll al principio
        recyclerView?.smoothScrollToPosition(0)

    }

    fun getPosition(id: Long): Int{
        val item = currentList.filter { i -> i.id == id }[0]
        return currentList.indexOf(item)
    }


    fun addLocations(newLocations: List<UserLocation>){
        locations.clear()
        locations.addAll(newLocations)
        submitList(locations.toList())
    }

    /**
     * Cuando es invocado, limpia toda
     * la lista de localizaciones de Usuario.
     */
    fun clearLocations(){
        submitList(null)
    }

    fun isEmpty(): Boolean {
        return locations.isEmpty()
    }
}