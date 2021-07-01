package es.uniovi.eii.contacttracker.location

/**
 * Enumerado que contiene los diferentes modos para la recepción
 * de las actualizaciones de localización.
 */
enum class LocationUpdateMode {
    CALLBACK_MODE, /* Callback de llamada al recibir una localización */
    PENDING_INTENT_MODE /* Pending intent al recibir una localización */
}