package es.uniovi.eii.contacttracker

/**
 * Clase Global en la que se almacenan todas las constantes de la aplicación.
 */
object Constants {

    // BROADCAST RECEIVER ACTIONS
    const val ACTION_GET_LOCATION = "Get Location"

    // EXTRAS
    const val EXTRA_LOCATION = "locationExtra"

    // LOCATION FOREGROUND SERVICE
    // ************* ACCIONES
    const val ACTION_START_LOCATION_SERVICE = "startLocationService" // Iniciar servicio
    const val ACTION_STOP_LOCATION_SERVICE = "stopLocationService" // Detener servicio
    // ************* EXTRAS
    const val EXTRA_COMMAND_FROM_ALARM = "commandFromAlarm" // Flag que indica si el comando procede de una alarma
    const val EXTRA_LOCATION_ALARM_ID = "extraLocationAlarmID" // ID de la alarma de localización.

    // BROADCAST RECEIVER PARA LA ALARMA DE COMPROBACIÓN DE CONTACTOS
    // **************************************************************
    const val EXTRA_RISK_CONTACT_ALARM = "extraRiskContactAlarm" // Extra para la alarma de comprobación de contactos

    // PARÁMETROS DEL MAPA
    // *******************
    // Zoom por Defecto
    const val DEFAULT_ZOOM = 18f

    // RADIO DE LA TIERRA (en km)
    // **************************
    const val EARTH_RADIUS = 6371

    /* Flag que indica la primera carga de la simulacíón */
    const val SIMULATION_LOCATIONS_LOADED = "SimulationLocationsLoaded"

}