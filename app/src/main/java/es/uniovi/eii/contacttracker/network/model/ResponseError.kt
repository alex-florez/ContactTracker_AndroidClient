package es.uniovi.eii.contacttracker.network.model

import es.uniovi.eii.contacttracker.util.Utils
import java.util.Date

/**
 * Clase de datos que modela una respuesta
 * de error enviada desde la API REST
 */
data class ResponseError(
        var code: Int, // Código de error
        var message: String, // Mensaje
        var timestamp: Long // Timestamp del error
) {
    override fun toString(): String {
        val date = Utils.formatDate(Date(timestamp), "dd-MM-yyyy HH:mm:ss")
        return "ResponseError(code=$code, message='$message', timestamp=$date)"
    }
}