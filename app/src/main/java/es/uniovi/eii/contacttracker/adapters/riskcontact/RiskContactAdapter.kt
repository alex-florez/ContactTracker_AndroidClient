package es.uniovi.eii.contacttracker.adapters.riskcontact

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.databinding.ItemCardRiskContactBinding
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.model.RiskLevel
import es.uniovi.eii.contacttracker.util.DateUtils

/* Constantes */
private const val DEFAULT_HOUR = "##:##:##" // Hora por defecto

/**
 * Adapter para los items del recycler view
 * de contactos de riesgo.
 */
class RiskContactAdapter(
    private val onShowInMapClick: OnShowInMapClick
) : ListAdapter<RiskContact, RiskContactAdapter.RiskContactViewHolder>(RiskContact.DIFF_CALLBACK) {

    /**
     * Interfaz listener del Click sobre el botón
     * de mostrar el contacto de riesgo en el mapa.
     */
    interface OnShowInMapClick {
        fun onClick(riskContact: RiskContact)
    }

    /**
     * ViewHolder para los items de Contactos de Riesgo.
     */
    class RiskContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         * View Binding.
         */
        private val binding = ItemCardRiskContactBinding.bind(itemView)

        /**
         * Vincula los datos del contacto de riesgo indicado con
         * los componentes del Card.
         */
        fun bindRiskContact(riskContact: RiskContact, onShowInMapClick: OnShowInMapClick) {
            setDates(riskContact)
            setExposeTime(riskContact)
            setMeanProximity(riskContact)
            setRiskPercent(riskContact)
            setCardColor(riskContact)
            setListeners(riskContact, onShowInMapClick)
        }

        /**
         * Establece las fechas de inicio y fin del contacto
         * en el Card.
         */
        private fun setDates(riskContact: RiskContact) {
            val startDate = DateUtils.formatDate(riskContact.startDate, "dd/MM/yyyy")
            var startHour = DEFAULT_HOUR
            var endHour = DEFAULT_HOUR
            if(riskContact.contactLocations.size > 1){ // Si hay más de un pto de contacto
                startHour = DateUtils.formatDate(riskContact.startDate, "HH:mm:ss")
                endHour = DateUtils.formatDate(riskContact.endDate, "HH:mm:ss")
            }
            binding.apply {
                txtContactDate.text = startDate
                txtContactStartHour.text = startHour
                txtContactEndHour.text = endHour
            }
        }

        /**
         * Establece el tiempo de exposición del contacto
         * en el Card.
         */
        private fun setExposeTime(riskContact: RiskContact) {
            val minSecs = DateUtils.getMinuteSecond(riskContact.exposeTime)
            val exposeTime = "${minSecs[0]} min ${minSecs[1]} sec"
            binding.txtContactExposeTime.text = exposeTime
        }

        /**
         * Establece la proximidad media del contacto.
         */
        private fun setMeanProximity(riskContact: RiskContact) {
            val meanProximity = "${riskContact.meanProximity} m"
            binding.txtContactMeanProximity.text = meanProximity
        }

        /**
         * Establece el porcentaje de riesgo del contacto.
         */
        private fun setRiskPercent(riskContact: RiskContact) {
            val riskPercent = "${riskContact.riskPercent} %"
            binding.txtRiskPercent.text = riskPercent
        }

        /**
         * Cambia el color del card en función
         * del nivel de riesgo del contacto.
         */
        private fun setCardColor(riskContact: RiskContact){
            val color = when(riskContact.riskLevel) {
                RiskLevel.AMARILLO -> {
                    ContextCompat.getDrawable(itemView.context, R.color.yellowWarning)
                }
                RiskLevel.NARANJA -> {
                    ContextCompat.getDrawable(itemView.context, R.color.orangeWarning)
                }
                RiskLevel.ROJO -> {
                    ContextCompat.getDrawable(itemView.context, R.color.redDanger)
                }
                else -> ContextCompat.getDrawable(itemView.context, R.color.greenOk)
            }
            if(color != null)
                binding.cardRiskContact.background = color
        }

        /**
         * Establece los listeners de eventos para los
         * componentes del Card.
         */
        private fun setListeners(riskContact: RiskContact, onShowInMapClick: OnShowInMapClick) {
            binding.btnShowContactInMap.setOnClickListener {
                onShowInMapClick.onClick(riskContact)
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiskContactViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_risk_contact, parent, false)
        return RiskContactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RiskContactViewHolder, position: Int) {
      holder.bindRiskContact(getItem(position), onShowInMapClick)
    }
}