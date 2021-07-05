package es.uniovi.eii.contacttracker

/**
 * Clase Global en la que se almacenan todas las
 * constantes de la aplicación.
 */
object Constants {

    // API Rest
    // ********
    // URL base localhost
    const val BASE_URL_LOCALHOST = "http://10.0.2.2:8080"

    // URL base deploy
    const val BASE_URL_DEPLOY = "https://contacttrackerbackend.azurewebsites.net"

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
    const val EXTRA_LOCATION_ALARM = "extraLocationAlarm" // Objeto LocationAlarm con los datos de las alarmas.
    const val EXTRA_LOCATION_ALARM_ID = "extraLocationAlarmID" // ID de la alarma de localización.

    // BROADCAST RECEIVER PARA LOS RESULTADOS DE LA COMPROBACIÓN DE CONTACTOS DE RIESGO
    // ********************************************************************************
    const val ACTION_GET_RISK_CONTACT_RESULT = "getRiskContactResult" // Acción para obtener el resultado de la comprobación.
    const val EXTRA_RISK_CONTACT_RESULT = "extraRiskContactResult" // Extra para el resultado de la comprobación.


    // PARÁMETROS DE CONFIGURACIÓN
    // ***************************
    // Nº de metros máximo de DESPLAZAMIENTO
    const val MAX_DISPLACEMENT = 100


    // PARÁMETROS DEL MAPA
    // *******************

    // Zoom por Defecto
    const val DEFAULT_ZOOM = 18f

    // RADIO DE LA TIERRA (en km)
    // **************************
    const val EARTH_RADIUS = 6371

    // NOTIFICACIONES
    // **************************
    const val ACTION_SHOW_RISK_CONTACT_RESULT = "actionShowRiskContactResult" // Acción enviada en un Intent para mostrar los resultados de una comprobación.
}