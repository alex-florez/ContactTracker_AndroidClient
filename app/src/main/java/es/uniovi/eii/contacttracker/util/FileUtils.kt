package es.uniovi.eii.contacttracker.util

import android.content.Context

/**
 * Clase estática que contiene métodos de utilidad
 * para trabajar con ficheros.
 */
object FileUtils {
    /**
     * Lee las líneas del fichero de texto cuyo nombre es pasado
     * como parámetro. El fichero de texto debe estar situado en
     * la carpeta Assets (app/src/main/assets).
     *
     * @param ctx Contexto para acceder a los assets.
     * @param filename Nombre del fichero de texto.
     * @return Lista con las líneas del fichero.
     */
    fun readFile(ctx: Context, filename: String): List<String> {
        var lines: List<String>
        ctx.assets.open(filename).bufferedReader().use {
            lines = it.readLines()
        }
        return lines
    }
}

