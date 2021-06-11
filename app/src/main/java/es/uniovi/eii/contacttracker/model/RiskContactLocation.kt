package es.uniovi.eii.contacttracker.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Clase de apoyo que irá embedida en la tabla de la Base de Datos
 * para almacenar unas coordenadas con su latitud y su longitud,
 * además del timestamp.
 */
data class Location(
        val lat: Double,
        val lng: Double,
        val timestamp: Date
)

/**
 * Representa un Par de localizaciones del propio usuario
 * y del positivo en el que ha habido un contacto de riesgo.
 */
@Entity
data class RiskContactLocation(
        @Embedded(prefix = "user_")
        val userLocation: Location, /* Localización de Usuario */
        @Embedded(prefix = "positive_")
        val positiveLocation: Location /* Localización del Positivo */
){
        @PrimaryKey(autoGenerate = true)
        var riskContactLocationId: Long? = null

        /* Foreign Key para el Contacto de Riesgo. */
        var rcId: Long? = null

}
