package es.uniovi.eii.contacttracker.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import es.uniovi.eii.contacttracker.util.DateUtils
import es.uniovi.eii.contacttracker.util.LocationUtils
import java.util.Date

/**
 * Clase que representa a un positivo en COVID-19.
 * Contiene tanto las localizaciones registradas por el dispositivo del usuario que
 * ha notificado ser positivo, como las fechas correspondientes a dichas localizaciones.
 * Opcionalmente, puede contener los datos personales del usuario.
 */
@Entity(tableName = "positives")
data class Positive(

        // ID único de la base de datos local
        @PrimaryKey(autoGenerate = true) var positiveID: Long? = null,

        @Expose
        @SerializedName("positiveCode")
        var positiveCode: String? = null, // Código autogenerado por Firestore

        @Expose
        @SerializedName("timestamp")
        var timestamp: Date = Date(), // Fecha de notificación del positivo

        @Expose
        @SerializedName("locations")
        @Ignore
        var locations: List<UserLocation> = mutableListOf(), // Localizaciones registradas

        @Expose
        @SerializedName("personalData")
        @Embedded
        var personalData: PersonalData? = null, // Datos personales (Opcional)

        @Expose
        @SerializedName("asymptomatic")
        var asymptomatic: Boolean = false, // Indica si el positivo es asintomático.

        @Expose
        @SerializedName("vaccinated")
        var vaccinated: Boolean = false // Indica si el positivo está vacunado.
)
