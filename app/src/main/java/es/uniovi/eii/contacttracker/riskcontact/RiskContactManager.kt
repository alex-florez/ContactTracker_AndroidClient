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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.App
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.activities.MainActivity
import es.uniovi.eii.contacttracker.model.*
import es.uniovi.eii.contacttracker.network.model.APIResult
import es.uniovi.eii.contacttracker.repositories.*
import es.uniovi.eii.contacttracker.riskcontact.detector.RiskContactDetector
import es.uniovi.eii.contacttracker.riskcontact.service.RiskContactForegroundService
import es.uniovi.eii.contacttracker.util.DateUtils
import java.util.*
import javax.inject.Inject

/* ID de la notificación con los resultados de la comprobación */
private const val RESULT_NOTIFICATION_ID = 1999
/* ID de la alarma de comprobación de contactos de riesgo */
private const val CHECK_ALARM_ID = 2999


/**
 * Clase que contiene toda la funcionalidad para gestionar
 * las comprobaciones de contactos de riesgo, incluyendo el
 * servicio en primer plano y las alarmas para las comprobaciones periódicas.
 */
class RiskContactManager @Inject constructor(
        private val detector: RiskContactDetector, // Detector de contactos de riesgo.
        private val locationRepository: LocationRepository, // Repositorio de localización.
        private val positiveRepository: PositiveRepository, // Repositorio de positivos.
        private val riskContactRepository: RiskContactRepository, // Repositorio de Contactos de Riesgo.
        private val configRepository: ConfigRepository, // Repositorio de configuración
        private val alarmManager: AlarmManager, // Manager de alarmas de Android
        @ApplicationContext private val ctx: Context
) {

    /**
     * Pending intent de la alarma de comprobación.
     */
    private var checkAlarmIntent: PendingIntent? = null

    /**
     * Ejecuta la comprobación de contactos de riesgo teniendo en cuenta los
     * parámetros de configuración establecidos para la comprobación.
     * Recupera las localizaciones correspondientes de los positivos
     * notificados en los últimos días y también las localizaciones del propio usuario.
     *
     * Al finalizar almacena en la base de datos local los resultados obtenidos.
     */
    suspend fun checkRiskContacts() {
        // Resultado de la comprobación
        val result = RiskContactResult()
        // Recuperar la configuración de la comprobación de contactos de riesgo.
        val config = configRepository.getRiskContactConfig()
        detector.setConfig(config) // Establecer la configuración al detector.

        /* Obtener el itinerario del propio usuario desde los últimos días indicados en el alcance */
//        val userItinerary = locationRepository.getItinerarySince(config.checkScope)
//        val userItinerary = pruebaUser()
        val userItinerary = Itinerary(locationRepository.getLastLocationsSince(config.checkScope))
        /* Obtener los positivos registrados con localizaciones de los últimos días según el alcance */
        var positives = mutableListOf<Positive>()
        when(val positivesResult = positiveRepository.getPositivesFromLastDays(config.checkScope)) {
            is APIResult.Success -> {
                positives = positivesResult.value.toMutableList()
            }
            else -> { // Error en la comprobación
                with(NotificationManagerCompat.from(ctx)){
                    notify(RESULT_NOTIFICATION_ID, createErrorNotification())
                }
                return // Dejar de ejecutar la comprobación
            }
        }
//        positives.add(Positive(null, Date(), listOf(), listOf(), null))
        /* Hacer la comprobación para cada positivo */
        positives.forEach { positive ->
            // Itinerario del positivo
//            val positiveItinerary = positive.getItinerary()
            val positiveItinerary = Itinerary(positive.locations)
//            val positiveItinerary = pruebaPositive()
            val contacts = detector.startChecking(userItinerary, positiveItinerary)
            if(contacts.isNotEmpty()){
                // Actualizar el resultado.
                result.riskContacts.addAll(contacts)
                result.numberOfPositives += 1 // Incrementar el número de positivos.
            }
        }
        /* Almacenar el resultado en la base de datos local. */
        riskContactRepository.insert(result)

        /* Mostrar Notificación con los resultados. */
        with(NotificationManagerCompat.from(ctx)){
            notify(RESULT_NOTIFICATION_ID, createNotification(result))
        }
        /* Emitir un Broadcast */
        sendBroadcast(result)
    }

    /**
     * Establece una nueva alarma de Android para realizar la
     * comprobación de contactos de riesgo.
     *
     * @param date Fecha de disparo de la alarma.
     */
    fun setPeriodicCheck(date: Date){
        // Crear hora de inicio
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, DateUtils.getFromDate(date, Calendar.HOUR_OF_DAY))
        cal.set(Calendar.MINUTE, DateUtils.getFromDate(date, Calendar.MINUTE))
        cal.set(Calendar.SECOND, 0)
        val startTime = cal.timeInMillis
        // Establecer pending intent
        checkAlarmIntent = getPendingIntentService(
            Intent(ctx, RiskContactForegroundService::class.java),
            CHECK_ALARM_ID
        )
        // Establecer alarma
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_DAY, checkAlarmIntent)
    }



    /**
     * Cancela la alarma de comprobación actualmente
     * establecida.
     */
    fun disablePeriodicCheck(){
        checkAlarmIntent?.let {
            alarmManager.cancel(checkAlarmIntent)
        }
    }


    /**
     * Crea una notificación visible para el usuario a partir de los resultados
     * obtenidos en la comprobación.
     *
     * @param riskContactResult Resultados de la comprobación de contactos de riesgo.
     * @return Notificación Android con un resumen de los resultados.
     */
    private fun createNotification(riskContactResult: RiskContactResult): Notification {
        // Color e icono grande de la notificación
        var color = ctx.getColor(R.color.greenOk)
        var largeIcon: Bitmap? = null
        when(riskContactResult.getHighestRiskContact().riskLevel){
            RiskLevel.VERDE -> {
                color = ctx.getColor(R.color.greenOk)
                ContextCompat.getDrawable(ctx, R.drawable.ic_healthy)?.let {
                    largeIcon = drawableToBitmap(it)
                }
            }
            RiskLevel.AMARILLO -> {
                color = ctx.getColor(R.color.yellowWarning)
                ContextCompat.getDrawable(ctx, R.drawable.ic_yellow_warning)?.let {
                    largeIcon = drawableToBitmap(it)
                }
            }
            RiskLevel.NARANJA -> {
                color = ctx.getColor(R.color.orangeWarning)
                ContextCompat.getDrawable(ctx, R.drawable.ic_orange_warning)?.let {
                    largeIcon = drawableToBitmap(it)
                }
            }
            RiskLevel.ROJO -> {
                color = ctx.getColor(R.color.redDanger)
                ContextCompat.getDrawable(ctx, R.drawable.ic_danger)?.let {
                    largeIcon = drawableToBitmap(it)
                }
            }
        }
        // Contenido de texto
        var textContent = ""
        var positivesText = if(riskContactResult.numberOfPositives > 1) "positivos" else "positivo"
        textContent = if(riskContactResult.riskContacts.isNotEmpty()){
            "Has estado en contacto con ${riskContactResult.numberOfPositives} " + positivesText +
                            ". Porcentaje de riesgo más alto: ${riskContactResult.getHighestRiskContact().riskPercent} %."
        } else { // No ha habido contactos de riesgo.
            ctx.getString(R.string.resultNotificationHealthy)
        }
        // Intent para ver los resultados de la comprobación.
        val pendingIntent: PendingIntent = Intent(ctx, MainActivity::class.java).let {
            it.action = Constants.ACTION_SHOW_RISK_CONTACT_RESULT
            it.putExtra(Constants.EXTRA_RISK_CONTACT_RESULT, riskContactResult)
            PendingIntent.getActivity(ctx, 0, it, 0)
        }

        return NotificationCompat.Builder(ctx, App.CHANNEL_ID_RISK_CONTACT_RESULT)
            .setContentTitle(ctx.getString(R.string.resultNotificationTitle))
            .setContentText(textContent)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setColorized(true)
            .setColor(color)
            .setLargeIcon(largeIcon)
            .setStyle(NotificationCompat.BigTextStyle().bigText(textContent))
            .build()
    }

    /**
     * Crea y devuelve una notificación de error en la comprobación.
     */
    private fun createErrorNotification(): Notification {
        return NotificationCompat.Builder(ctx, App.CHANNEL_ID_RISK_CONTACT_RESULT)
            .setContentTitle(ctx.getString(R.string.resultNotificationCheckingErrorTitle))
            .setContentText(ctx.getString(R.string.resultNotificationCheckingError))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(NotificationCompat.BigTextStyle().bigText(ctx.getString(R.string.resultNotificationCheckingError)))
            .build()
    }

    /**
     * Método privado que devuelve el PendingIntent correspondiente
     * al Intent pasado como parámetro en función de la versión
     * del dispositivo.
     *
     * @param intent Intent con el servicio de localización.
     * @param id Id asociado al PendingIntent.
     *
     */
    private fun getPendingIntentService(intent: Intent, id: Int): PendingIntent {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            PendingIntent.getForegroundService(ctx, id, intent, 0)
        } else {
            PendingIntent.getService(ctx, id, intent, 0)
        }
    }

    /**
     * Transforma el Drawable indicado como parámetro en un Bitmap.
     *
     * @param drawable Objeto Drawable.
     * @return Bitmap transformado.
     */
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0,0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

//    private fun pruebaUser(): Itinerary {
//       val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
//       // Generar itinerarios de prueba
//
//       val u1 = UserLocation(11, 43.537852, -5.904168, 20.0, "", df.parse("08/06/2021 12:10:00"))
//       val u2 = UserLocation(12, 43.537729, -5.904189, 20.0, "", df.parse("08/06/2021 12:12:23"))
//       val u3 = UserLocation(13, 43.537628, -5.904214, 20.0, "", df.parse("08/06/2021 12:14:04"))
//       val u4 = UserLocation(14, 43.537569, -5.904231, 20.0, "", df.parse("08/06/2021 12:15:50")) // <-- Start
//       val u5 = UserLocation(15, 43.537569, -5.904165, 20.0, "", df.parse("08/06/2021 12:16:02"))
//       val u6 = UserLocation(16, 43.537596, -5.904063, 20.0, "", df.parse("08/06/2021 12:17:00"))
//       val u7 = UserLocation(17, 43.537634, -5.903972, 20.0, "", df.parse("08/06/2021 12:17:42"))// <-- End
//       val u8 = UserLocation(18, 43.537682, -5.903906, 20.0, "", df.parse("08/06/2021 12:18:10"))
//       val u9 = UserLocation(19, 43.537710, -5.903835, 20.0, "", df.parse("08/06/2021 12:19:13"))
//       val u10 = UserLocation(110, 43.537722, -5.903768, 20.0, "", df.parse("08/06/2021 12:19:57"))
//       val u11 = UserLocation(111, 43.537689, -5.903714, 20.0, "", df.parse("08/06/2021 12:20:36")) // <-- Start
//       val u12 = UserLocation(112, 43.537640, -5.903636, 20.0, "", df.parse("08/06/2021 12:21:15"))
//       val u13 = UserLocation(113, 43.537664, -5.903537, 20.0, "", df.parse("08/06/2021 12:22:20")) // <-- End
//
//       // Usuario
//       val userLocations = mutableMapOf<String, List<UserLocation>>()
//       val userList = listOf(u1,u2,u3,u4,u5,u6,u7, u8, u9, u10, u11, u12, u13)
//       userLocations["2021-06-08"] = userList
//       return Itinerary(userLocations)
//    }
//
//    private fun pruebaPositive(): Itinerary {
//        val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
//       val p1 = UserLocation(21, 43.537562, -5.904420, 20.0, "", df.parse("08/06/2021 12:08:03"))
//       val p2 = UserLocation(22, 43.537563, -5.904329, 20.0, "", df.parse("08/06/2021 12:10:03"))
//       val p3 = UserLocation(23, 43.537558, -5.904227, 20.0, "", df.parse("08/06/2021 12:15:02")) // <-- Start
//       val p4 = UserLocation(24, 43.537568, -5.904114, 20.0, "", df.parse("08/06/2021 12:16:20"))
//       val p5 = UserLocation(25, 43.537601, -5.904012, 20.0, "", df.parse("08/06/2021 12:18:03"))
//       val p6 = UserLocation(26, 43.537571, -5.903884, 20.0, "", df.parse("08/06/2021 12:18:20")) // <-- End
//       val p7 = UserLocation(27, 43.537630, -5.903803, 20.0, "", df.parse("08/06/2021 12:18:56"))
//       val p8 = UserLocation(28, 43.537676, -5.903737, 20.0, "", df.parse("08/06/2021 12:19:58")) // <-- Start
//       val p9 = UserLocation(29, 43.537638, -5.903654, 20.0, "", df.parse("08/06/2021 12:20:57"))
//       val p10 = UserLocation(210, 43.537583, -5.903629, 20.0, "", df.parse("08/06/2021 12:21:46")) // <-- End
//
//       // Positivo
//       val positiveLocations = mutableMapOf<String, List<UserLocation>>()
//       val positiveList = listOf(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10)
//       positiveLocations["2021-06-08"] = positiveList
//       return Itinerary(positiveLocations)
//    }

    /**
     * Emite un Broadcast Receiver con el resultado de la comprobación.
     */
    private fun sendBroadcast(riskContactResult: RiskContactResult){
        val intent = Intent()
        intent.action = Constants.ACTION_GET_RISK_CONTACT_RESULT
        intent.putExtra(Constants.EXTRA_RISK_CONTACT_RESULT, riskContactResult)
        ctx.sendBroadcast(intent)
    }
}