package es.uniovi.eii.contacttracker.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Clase de apoyo que irá embedida en la tabla de la Base de Datos
 * para almacenar unas coordenadas con su latitud y su longitud,
 * además del timestamp.
 */
@Parcelize
data class ContactPoint(
        val name: String,
        val lat: Double,
        val lng: Double,
        val timestamp: Date
) : Parcelable

/**
 * Representa un Par de localizaciones del propio usuario
 * y del positivo en el que ha habido un contacto de riesgo.
 */
@Parcelize
@Entity
data class RiskContactLocation(
        @Embedded(prefix = "user_")
        val userContactPoint: ContactPoint, /* Localización de Usuario */
        @Embedded(prefix = "positive_")
        val positiveContactPoint: ContactPoint /* Localización del Positivo */
) : Parcelable {
        @PrimaryKey(autoGenerate = true)
        var riskContactLocationId: Long? = null

        /* Foreign Key para el Contacto de Riesgo. */
        var rcId: Long? = null

}
