package es.uniovi.eii.contacttracker.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.model.RiskContactLocation

/**
 * Clase auxiliar para representar la relación entre un
 * contacto de riesgo y sus localizaciones de contacto.
 */
data class RiskContactWithLocations(
    @Embedded val riskContact: RiskContact,
    @Relation(
            parentColumn = "riskContactId",
            entityColumn = "riskContactId"
    )
        val riskContactLocations: List<RiskContactLocation>
)