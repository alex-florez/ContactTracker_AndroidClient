package es.uniovi.eii.contacttracker.location

/**
 * Enumerado que contiene los diferentes modos para la recepción
 * de las actualizaciones de localización.
 */
enum class LocationUpdateMode {
    CALLBACK_MODE, PENDING_INTENT_MODE
}