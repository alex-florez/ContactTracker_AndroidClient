package es.uniovi.eii.contacttracker.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date
/**
 * Representa un par de localizaciones en el mapa asociadas a un
 * positivo y al propio usuario del dispositivo respectivamente.
 */
@Parcelize
@Entity(tableName = "risk_contact_locations")
data class RiskContactLocation(
        @PrimaryKey(autoGenerate = true) var riskContactLocationId: Long? = null,
        @Embedded(prefix = "user_")
        val userContactPoint: Point, /* Localización de Usuario */
        @Embedded(prefix = "positive_")
        val positiveContactPoint: Point, /* Localización del Positivo */
        var riskContactId: Long? = null /* Foreign Key para el Contacto de Riesgo al que pertenece este punto de contacto */
) : Parcelable