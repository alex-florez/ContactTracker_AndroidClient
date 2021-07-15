package es.uniovi.eii.contacttracker.util

import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import es.uniovi.eii.contacttracker.R

/**
 * Clase que contiene diferentes métodos de utilidad
 * relacionados con el framework de Android.
 */
object AndroidUtils {

    /**
     * Devuelve true si el permiso de Android pasado como
     * parámetro ha sido concedido por el usuario.
     *
     * @param ctx contexto de Android
     * @param permission Permiso a comprobar
     * @return true si está concedido
     */
    fun check(ctx: Context, permission: String): Boolean{
        return ContextCompat.checkSelfPermission(ctx, permission) == PackageManager.PERMISSION_GRANTED
    }


    /**
     * Crea un Snackbar en la vista indicada, con el texto indicado, la duración indicada
     * y anclado sobre el menú de navegación inferior.
     *
     * @param text Texto a mostrar en el Snackbar.
     * @param length Duración del Snackbar.
     * @param view Vista padre del Snackbar.
     * @param activity Referencia a la Activity.
     */
    fun snackbar(text: String, length: Int, view: View, activity: FragmentActivity) {
        Snackbar.make(view, text, length).apply {
            anchorView = activity.findViewById(R.id.bottomNavigationView)// Anclarlo al menú inferior
            show()
        }
    }
}