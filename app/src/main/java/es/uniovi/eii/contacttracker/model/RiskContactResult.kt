package es.uniovi.eii.contacttracker.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import es.uniovi.eii.contacttracker.adapters.results.RiskContactResultDiffCallback
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Clase que representa el resultado de la comprobación de contactos de riesgo.
 * Contiene una lista de Tramos de contacto, en los cuales ha habido riesgo
 * de contacto con el positivo.
 */
@Parcelize
@Entity
class RiskContactResult(
        @PrimaryKey(autoGenerate = true) var resultId: Long? = null,
        @Ignore var riskContacts: MutableList<RiskContact> = mutableListOf(), /* Contactos de riesgo */
        var numberOfPositives: Int = 0, /* Nº de positivos con los que se ha entrado en contacto */
        var timestamp: Date = Date() /* Timestamp de finalización de la comprobación */
) : Parcelable {

    companion object {
        /**
         * Variable estática que almacena el callback de diferencias para los objetos de
         * resultados de contactos de riesgo.
         */
        val DIFF_CALLBACK: DiffUtil.ItemCallback<RiskContactResult> = RiskContactResultDiffCallback()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RiskContactResult

        if (riskContacts != other.riskContacts) return false
        if (numberOfPositives != other.numberOfPositives) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = riskContacts.hashCode()
        result = 31 * result + numberOfPositives
        result = 31 * result + timestamp.hashCode()
        return result
    }

    override fun toString(): String {
        return "RiskContactResult(resultId=$resultId, riskContacts=$riskContacts, numberOfPositives=$numberOfPositives, timestamp=$timestamp)"
    }


}