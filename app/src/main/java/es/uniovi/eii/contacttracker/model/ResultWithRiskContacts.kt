package es.uniovi.eii.contacttracker.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

/**
 * Clase de apoyo para representar la relación 1 a n
 * entre el resultado de la comprobación y los contactos de riesgo.
 */
data class ResultWithRiskContacts(
        @Embedded val riskContactResult: RiskContactResult,
        @Relation(
                entity = RiskContact::class,
                parentColumn = "resultId",
                entityColumn = "riskContactResultId"
        )
        val riskContacts: List<RiskContactWithLocation>
)