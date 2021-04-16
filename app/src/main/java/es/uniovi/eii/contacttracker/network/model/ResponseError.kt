package es.uniovi.eii.contacttracker.network.model

import java.sql.Date
import java.text.SimpleDateFormat

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
        val date = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(timestamp)
        return "ResponseError(code=$code, message='$message', timestamp=$date)"
    }
}