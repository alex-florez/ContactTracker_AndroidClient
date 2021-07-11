package es.uniovi.eii.contacttracker.room.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import es.uniovi.eii.contacttracker.model.Positive
import es.uniovi.eii.contacttracker.model.UserLocation

/**
 * Clase de apoyo que representa una relación 1 a n
 * entre un objeto Positive y una lista de localizciones de usuario.
 */
data class PositiveWithLocations (
    @Embedded val positive: Positive,
    @Relation(
        parentColumn = "positiveID",
        entityColumn = "userlocationID",
        associateBy = Junction(PositiveUserLocationCrossRef::class)
    )
    val locations: List<UserLocation>
)