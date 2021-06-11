package es.uniovi.eii.contacttracker.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Clase que representa el resultado de la comprobación de contactos de riesgo.
 * Contiene una lista de Tramos de contacto, en los cuales ha habido riesgo
 * de contacto con el positivo.
 */
@Entity
class RiskContactResult {

    @PrimaryKey(autoGenerate = true)
    var resultId: Long? = null

    /**
     * Lista de contactos de riesgo.
     */
    @Ignore
    var riskContacts: MutableList<RiskContact> = mutableListOf()

    /**
     * Número de positivos con los que se ha entrado en contacto.
     */
    var numberOfPositives: Int = 0

    /**
     * Timestamp de finalización de la comprobación.
     */
    var timestamp: Date = Date()

}