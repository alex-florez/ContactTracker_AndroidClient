package es.uniovi.eii.contacttracker.room.mappers

import es.uniovi.eii.contacttracker.model.Positive
import es.uniovi.eii.contacttracker.room.relations.PositiveWithLocations

/**
 * Transforma el objeto auxiliar que representa la relación 1 a n
 * en un objeto Positive.
 *
 * @param positiveWithLocations Objeto wrapper con el positivo y las localizaciones.
 * @return Objeto positivo ya transformado.
 */
fun toPositive(positiveWithLocations: PositiveWithLocations): Positive {
    return Positive(
        positiveWithLocations.positive.positiveID,
        positiveWithLocations.positive.positiveCode,
        positiveWithLocations.positive.timestamp,
        positiveWithLocations.locations,
        positiveWithLocations.positive.personalData
    )
}