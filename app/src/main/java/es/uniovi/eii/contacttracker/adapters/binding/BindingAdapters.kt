package es.uniovi.eii.contacttracker.adapters.binding

import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.BindingAdapter
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.model.RiskLevel
import es.uniovi.eii.contacttracker.util.DateUtils

/**
 * Funciones Adapter para personalizar los atributos de los componentes de los layouts XML.
 */

/**
 * Alterna la visibilidad de la vista especificada.
 */
@BindingAdapter("visibility")
fun setVisibility(view: View, visibility: Boolean) {
    if(visibility)
        view.visibility = View.VISIBLE
    else
        view.visibility = View.GONE
}

/**
 * Establece el color de fondo y el color de texto de la
 * vista indicada en función del nivel de riesgo pasado como
 * atributo de la vista en el layout.
 */
@BindingAdapter("riskLevelBackground")
fun setRiskLevelBackground(view: TextView, riskLevel: RiskLevel){
    when(riskLevel){
        RiskLevel.VERDE -> {
            view.background = AppCompatResources.getDrawable(view.context, R.color.greenOk)
            view.setTextColor(view.context.getColor(R.color.white))
        }
        RiskLevel.AMARILLO -> {
            view.background = AppCompatResources.getDrawable(view.context, R.color.yellowWarning)
            view.setTextColor(view.context.getColor(R.color.black))
        }
        RiskLevel.NARANJA -> {
            view.background = AppCompatResources.getDrawable(view.context, R.color.orangeWarning)
            view.setTextColor(view.context.getColor(R.color.white))
        }
        RiskLevel.ROJO -> {
            view.background = AppCompatResources.getDrawable(view.context, R.color.redDanger)
            view.setTextColor(view.context.getColor(R.color.white))
        }
    }
}

/**
 * Establece un tiempo con formato de minutos y segundos en el TextView
 * a partir de los milisegundos indicados.
 */
@BindingAdapter("time")
fun setTime(view: TextView, millis: Long) {
    val minSec = DateUtils.getMinuteSecond(millis)
    view.text = view.context.getString(R.string.minSecsText, minSec[0], minSec[1])
}