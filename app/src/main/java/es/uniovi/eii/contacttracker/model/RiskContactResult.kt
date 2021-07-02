package es.uniovi.eii.contacttracker.model

import android.os.Parcelable
import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import es.uniovi.eii.contacttracker.adapters.results.RiskContactResultDiffCallback
import es.uniovi.eii.contacttracker.util.Utils
import kotlinx.parcelize.Parcelize
import okhttp3.internal.Util
import java.util.*

/**
 * Clase que representa el resultado de la comprobación de contactos de riesgo.
 * Contiene una lista de Tramos de contacto, en los cuales ha habido riesgo
 * de contacto con el positivo.
 */
@Parcelize
@Entity(tableName = "risk_contact_results")
data class RiskContactResult(
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


    /**
     * Devuelve el contacto de riesgo de mayor peligro, es decir,
     * el que tenga mayor Score de riesgo.
     */
    fun getHighestRiskContact(): RiskContact {
        /* Ordenar por riskScore descendente */
        val ordered = riskContacts.sortedByDescending { it.riskPercent }
        if(ordered.isNotEmpty())
            return ordered[0] // Devolver el primero
        return RiskContact()
    }

    /**
     * Devuelve el porcentaje total de riesgo medio en función
     * de los riesgos de todos los contactos de riesgo.
     *
     * @return Media de riesgo total.
     */
    fun getTotalMeanRisk(): Double {
        var mean = 0.0
        if(riskContacts.isEmpty())
            return mean
        riskContacts.forEach {
            mean += it.riskPercent
        }
        return Utils.round(mean/riskContacts.size, 4)
    }

    /**
     * Devuelve la media total de tiempo de exposición en milisegundos.
     *
     * @return Milisegundos medios de tiempo de exposición.
     */
    fun getTotalMeanExposeTime(): Long {
        var mean = 0L
        if(riskContacts.isEmpty())
            return mean
        riskContacts.forEach {
            mean += it.exposeTime
        }

        return Utils.round((mean/riskContacts.size).toDouble(), 4).toLong()
    }

    /**
     * Devuelve la proximidad media total en metros, en función
     * de las proximidades medias de los contactos de riesgo.
     *
     * @return Distancia de proximidad media en metros.
     */
    fun getTotalMeanProximity(): Double {
        var mean = 0.0
        if(riskContacts.isEmpty())
            return mean
        riskContacts.forEach {
            mean += it.meanProximity
        }
        return Utils.round(mean/riskContacts.size, 4)
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