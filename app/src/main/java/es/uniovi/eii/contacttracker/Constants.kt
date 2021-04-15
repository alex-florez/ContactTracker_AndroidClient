package es.uniovi.eii.contacttracker

/**
 * Clase Global en la que se almacenan todas las
 * constantes de la aplicación.
 */
object Constants {

    // API Rest
    // ********
    // URL base localhost
    val BASE_URL_LOCALHOST = "http://10.0.2.2:8080"

    // URL base deploy
    val BASE_URL_DEPLOY = "https://contacttrackerbackend.azurewebsites.net"

    // BROADCAST RECEIVER ACTIONS
    val ACTION_GET_LOCATION = "Get Location"

    // EXTRAS
    val EXTRA_LOCATION = "locationExtra"

    // LOCATION FOREGROUND SERVICE
    // ************* ACCIONES
    val ACTION_START_LOCATION_SERVICE = "startLocationService" // Iniciar servicio
    val ACTION_STOP_LOCATION_SERVICE = "stopLocationService" // Detener servicio
    // ************* EXTRAS
    val EXTRA_COMMAND_FROM_ALARM = "commandFromAlarm" // Flag que indica si el comando procede de una alarma
    val EXTRA_LOCATION_ALARM = "extraLocationAlarm" // Objeto LocationAlarm con los datos de las alarmas.
    val EXTRA_LOCATION_ALARM_ID = "extraLocationAlarmID" // ID de la alarma de localización.


}