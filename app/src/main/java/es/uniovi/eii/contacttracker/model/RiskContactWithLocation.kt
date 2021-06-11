package es.uniovi.eii.contacttracker.model

import androidx.room.Embedded
import androidx.room.Relation

data class RiskContactWithLocation(
        @Embedded val riskContact: RiskContact,
        @Relation(
            parentColumn = "riskContactId",
            entityColumn = "rcId"
        )
        val riskContactLocations: List<RiskContactLocation>
)