package es.uniovi.eii.contacttracker.notifications

import android.app.Notification
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.App
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.model.RiskLevel
import javax.inject.Inject

/* IDs para las notificaciones */
private const val RESULT_NOTIFICATION_ID = 1

/**
 * Manager que gestiona todas las notificaciones que se generan
 * dentro de la propia aplicación de Contact Tracker.
 */
class InAppNotificationManager @Inject constructor(
    @ApplicationContext private val ctx: Context
) {

    /**
     * Muestra una notificación con el resultado de la comproabación de contactos
     * de riesgo pasado como parámetro, a través del Manager de Notificaciones
     * de Android.
     *
     * @param result Resultado de la comprobación de contactos de riesgo.
     */
    fun showRiskContactResultNotification(result: RiskContactResult) {
        with(NotificationManagerCompat.from(ctx)) {
            notify(RESULT_NOTIFICATION_ID, createRiskContactResultNotification(result))
        }
    }

    /**
     * Muestra una notificación de error en la comproabación de contactos
     * de riesgo pasado como parámetro, a través del Manager de Notificaciones
     * de Android.
     */
    fun showRiskContactCheckErrorNotification() {
        with(NotificationManagerCompat.from(ctx)) {
            notify(RESULT_NOTIFICATION_ID, createRiskContactCheckErrorNotification())
        }
    }

    /**
     * Crea una notificación visible para el usuario a partir de los resultados
     * obtenidos en la comprobación de contactos de riesgo.
     *
     * @param result Resultados de la comprobación de contactos de riesgo.
     * @return Notificación Android con un resumen de los resultados.
     */
    private fun createRiskContactResultNotification(result: RiskContactResult): Notification {
        // Color e icono grande de la notificación
        val colorIcon = getNotificationColorAndIcon(result.getHighestRiskContact().riskLevel)
        var largeIcon: Bitmap? = null
        ContextCompat.getDrawable(ctx, colorIcon.second)?.let {
            largeIcon = drawableToBitmap(it)
        }

        // Contenido de texto
        val textContent = if(result.riskContacts.isNotEmpty()){ // Peligro: contactos de riesgo detectados.
            ctx.resources.getQuantityString(
                R.plurals.resultNotificationRiskContact,
                result.numberOfPositives, result.numberOfPositives) + " " +
                    ctx.getString(R.string.resultNotificationHighestRiskPercent, result.getHighestRiskContact().riskPercent)
        } else { // No ha habido contactos de riesgo.
            ctx.getString(R.string.resultNotificationHealthy)
        }
        // Intent para ver los resultados de la comprobación.
        val pendingIntent = NavDeepLinkBuilder(ctx)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.resultDetailsFragment)
            .setArguments(bundleOf("result" to result))
            .createPendingIntent()

        return NotificationCompat.Builder(ctx, App.CHANNEL_ID_RISK_CONTACT_RESULT)
            .setContentTitle(ctx.getString(R.string.resultNotificationTitle))
            .setContentText(textContent)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setColorized(true)
            .setColor(colorIcon.first)
            .setLargeIcon(largeIcon)
            .setStyle(NotificationCompat.BigTextStyle().bigText(textContent))
            .build()
    }

    /**
     * Crea y devuelve una notificación que representa un error durante la
     * comprobación de contactos de riesgo.
     */
    private fun createRiskContactCheckErrorNotification(): Notification {
        return NotificationCompat.Builder(ctx, App.CHANNEL_ID_RISK_CONTACT_RESULT)
            .setContentTitle(ctx.getString(R.string.resultNotificationCheckingErrorTitle))
            .setContentText(ctx.getString(R.string.resultNotificationCheckingError))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setStyle(NotificationCompat.BigTextStyle().bigText(ctx.getString(R.string.resultNotificationCheckingError)))
            .build()
    }

    /**
     * Devuelve un par con el color y el icono para la notificación de los resultados
     * de la comprobación que se corresponde con el nivel de riesgo pasado como parámetro.
     *
     * @param riskLevel Nivel de riesgo.
     * @return Par con el color y el icono correspondiente al nivel de riesgo.
     */
    private fun getNotificationColorAndIcon(riskLevel: RiskLevel): Pair<Int, Int> {
        var color = ctx.getColor(R.color.greenOk)
        var icon = R.drawable.ic_healthy
        when(riskLevel) {
            RiskLevel.VERDE -> {
                color = ctx.getColor(R.color.greenOk)
                icon = R.drawable.ic_healthy
            }
            RiskLevel.AMARILLO -> {
                color = ctx.getColor(R.color.yellowWarning)
                icon = R.drawable.ic_yellow_warning
            }
            RiskLevel.NARANJA -> {
                color = ctx.getColor(R.color.orangeWarning)
                icon = R.drawable.ic_orange_warning
            }
            RiskLevel.ROJO -> {
                color = ctx.getColor(R.color.redDanger)
                icon = R.drawable.ic_danger
            }
        }
        return Pair(color, icon)
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
}