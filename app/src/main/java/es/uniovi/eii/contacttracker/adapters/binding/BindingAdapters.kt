package es.uniovi.eii.contacttracker.adapters.binding

import android.view.View
import androidx.databinding.BindingAdapter

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