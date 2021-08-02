package es.uniovi.eii.contacttracker.model

/**
 * Enumerado que representa los diferentes tipos de errores de lógica de
 * negocio que pueden surgir en la aplicación.
 */
enum class Error {
    /* Errores genéricos */
    SERVER_ERROR,
    TIMEOUT,

    /* Alarmas de Localización */
    INVALID_ALARM,
    ALARM_COLLISION,

    /* Alarmas de Comprobación de Contactos de Riesgo */
    RISK_CONTACT_ALARM_COLLISION,
    RISK_CONTACT_ALARM_COUNT_LIMIT_EXCEEDED,

    /* Notificación de Positivos */
    CANNOT_NOTIFY,
    NO_LOCATIONS_TO_NOTIFY,
    NOTIFICATION_LIMIT_EXCEEDED

}