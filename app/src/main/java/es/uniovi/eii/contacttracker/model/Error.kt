package es.uniovi.eii.contacttracker.model

/**
 * Enumerado que representa los diferentes tipos de errores de lógica de
 * negocio que pueden surgir en la aplicación.
 */
enum class Error {
    /* Errores genéricos */
    SERVER_ERROR,
    TIMEOUT,

    /* Alarmas de localización */
    INVALID_ALARM,
    ALARM_COLLISION
}