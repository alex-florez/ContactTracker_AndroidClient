package es.uniovi.eii.contacttracker.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Clase de utilidad que contiene métodos para
 * comprobar permisos de Android y otras utilidades.
 */
object PermissionUtils {

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
}