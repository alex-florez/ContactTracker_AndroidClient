package es.uniovi.eii.contacttracker.riskcontact.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import es.uniovi.eii.contacttracker.riskcontact.service.RiskContactForegroundService

/**
 * Broadcast Receiver que será disparado por la alarma programada a una
 * hora determinada para iniciar el servicio de comprobación de contactos
 * de riesgo.
 */
class StartRiskContactCheckReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        /* Reprogramar la alarma para el siguiente día */
        resetAlarm()

        /* Iniciar Foreground Service para la comprobación */
        if(context != null)
            startCheckingService(context)
    }

    /**
     * Reestablece la alarma para que sea ejecutada de nuevo al siguiente día
     * de la hora original a la que fue disparada.
     */
    private fun resetAlarm() {

    }

    /**
     * Inicializa el Foreground Service para ejecutar la comprobación
     * de contactos de riesgo.
     */
    private fun startCheckingService(ctx: Context) {
        val i = Intent(ctx, RiskContactForegroundService::class.java)
        ContextCompat.startForegroundService(ctx, i)
    }
}