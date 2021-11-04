package es.uniovi.eii.contacttracker.riskcontact

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.App
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.activities.MainActivity
import es.uniovi.eii.contacttracker.fragments.riskcontacts.CheckMode
import es.uniovi.eii.contacttracker.model.*
import es.uniovi.eii.contacttracker.network.model.APIResult
import es.uniovi.eii.contacttracker.network.model.CheckResult
import es.uniovi.eii.contacttracker.notifications.InAppNotificationManager
import es.uniovi.eii.contacttracker.repositories.*
import es.uniovi.eii.contacttracker.riskcontact.alarms.StartRiskContactCheckReceiver
import es.uniovi.eii.contacttracker.riskcontact.detector.RiskContactDetector
import es.uniovi.eii.contacttracker.riskcontact.service.RiskContactForegroundService
import es.uniovi.eii.contacttracker.util.DateUtils
import java.util.*
import javax.inject.Inject

/* ID de la notificación con los resultados de la comprobación */
private const val RESULT_NOTIFICATION_ID = 1999

/* TAG */
private const val TAG = "RiskContactManager"

/**
 * Clase que gestiona la comprobación de contactos de riesgo. Recupera las localizaciones
 * del propio usuario almacenadas en local y las localizaciones de los positivos de la
 * nube para compararlas y detectar contactos de riesgo.
 *
 * Utiliza los parámetros de la configuración de la comprobación de contactos almacenados
 * tanto en el dispositivo local como en los ajustes de la nube modificados por los administradores.
 */
class RiskContactManager @Inject constructor(
        private val detector: RiskContactDetector, // Detector de contactos de riesgo.
        private val locationRepository: LocationRepository, // Repositorio de localización.
        private val positiveRepository: PositiveRepository, // Repositorio de positivos.
        private val riskContactRepository: RiskContactRepository, // Repositorio de Contactos de Riesgo.
        private val configRepository: ConfigRepository, // Repositorio de configuración.
        private val statisticsRepository: StatisticsRepository, // Repositorio de estadísticas.
        private val inAppNotificationManager: InAppNotificationManager // Manager de notificaciones internas.
//        @ApplicationContext private val ctx: Context
) {

    /**
     * Ejecuta la comprobación de contactos de riesgo teniendo en cuenta los
     * parámetros de configuración establecidos para la comprobación.
     * Recupera las localizaciones correspondientes de los positivos
     * notificados en los últimos días y también las localizaciones del propio usuario.
     *
     * Al finalizar almacena en la base de datos local los resultados obtenidos.
     *
     * @param date Fecha de referencia en la que se realiza la comprobación.
     */
    suspend fun checkRiskContacts(date: Date) {
        // Resultado de la comprobación
        val result = RiskContactResult()
        // Recuperar la configuración de la comprobación de contactos de riesgo.
        val config = configRepository.getRiskContactConfig()
        detector.setConfig(config) // Establecer la configuración al detector.

        /* Obtener el itinerario del propio usuario desde los últimos días indicados en el alcance */
        val userItinerary = Itinerary(locationRepository.getLastLocationsSince(config.checkScope, date), "Usuario")
        /* Obtener los positivos registrados con localizaciones de los últimos días según el alcance */
        var positives: List<Positive> = mutableListOf()
        when(val positivesResult = positiveRepository.getPositivesFromLastDays(config.checkScope)) {
            is APIResult.Success -> {
                // Filtrar positivos: ignorar aquellos que hayan sido notificados por el propio usuario.
                positives = filterPositives(positivesResult.value.toList())
            }
            is APIResult.HttpError -> {
                inAppNotificationManager.showRiskContactCheckErrorNotification()
                return // Dejar de ejecutar la comprobación
            }
            is APIResult.NetworkError -> {
                inAppNotificationManager.showRiskContactCheckErrorNotification()
                return // Dejar de ejecutar la comprobación
            }
        }
//        positives.add(Positive(null, Date(), listOf(), listOf(), null))
        /* Hacer la comprobación para cada positivo */
        var index = 0
        positives.forEach { positive ->
            index++
            // Itinerario del positivo
            val positiveItinerary = Itinerary(positive.locations, "Positivo $index")
            val contacts = detector.startChecking(userItinerary, positiveItinerary)
            if(contacts.isNotEmpty()){
                // Actualizar el resultado.
                result.riskContacts.addAll(contacts)
                result.numberOfPositives += 1 // Incrementar el número de positivos.
            }
        }
        /* Almacenar el resultado en la base de datos local. */
        val id = riskContactRepository.insert(result)
        result.resultId = id
        /* Mostrar Notificación con los resultados. */
        inAppNotificationManager.showRiskContactResultNotification(result)
        /* Registrar los datos principales del resultado en la nube para cometidos estadísticos */
        val checkResult = CheckResult(
            result.timestamp.time,
            result.getTotalMeanRisk(),
            result.getTotalMeanExposeTime(),
            result.getTotalMeanProximity()
        )
        when(val response = statisticsRepository.registerRiskContactResult(checkResult)) {
            is APIResult.Success -> {
                Log.d(TAG, response.value.msg)
            }
            else -> {
                Log.d(TAG, "No se ha podido registrar el resultado de la comprobación.")
            }
        }
    }

    /**
     * Establece el modo de comprobación.
     *
     * @param checkMode Nuevo modo de comprobación.
     */
    fun setCheckMode(checkMode: CheckMode) {
        riskContactRepository.setCheckMode(checkMode)
    }

    /**
     * Devuelve el modo de comprobación actual.
     *
     * @return Modo de comprobación actual.
     */
    fun getCheckMode(): CheckMode {
        return riskContactRepository.getCheckMode()
    }

    /**
     * Compara los positivos obtenidos de la nube con los positivos almacenados
     * en el propio dispositivo (local) mediante el CÓDIGO de POSITIVO. Devuelve
     * solo aquellos positivos que no hayan sido notificados por el propio usuario y que
     * por tanto no estén almacenados en local.
     *
     * @param positives Lista con todos los positivos recuperados de la nube.
     * @return Lista filtrada con los positivos que no hayan sido notificados por el propio usuario.
     */
    private suspend fun filterPositives(positives: List<Positive>): List<Positive> {
        val localPositives = positiveRepository.getAllLocalPositiveCodes()
        return positives.filter {
            !localPositives.contains(it.positiveCode)
        }
    }
}