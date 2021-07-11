package es.uniovi.eii.contacttracker.room.relations

import androidx.room.Entity

/**
 * Clase que representa una referencia cruzada entre un objeto
 * Positive y un objeto UserLocation. Modela una relación N a N.
 */
@Entity(primaryKeys = ["positiveID", "userlocationID"], tableName = "positive_user_location_cross_refs")
data class PositiveUserLocationCrossRef (
    val positiveID: Long,
    val userlocationID: Long
)