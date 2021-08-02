package es.uniovi.eii.contacttracker.riskcontact.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.riskcontact.service.RiskContactForegroundService
import es.uniovi.eii.contacttracker.util.AndroidUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.invoke.ConstantCallSite
import javax.inject.Inject

/**
 * Broadcast Receiver que será disparado por la alarma programada a una
 * hora determinada para iniciar el servicio de comprobación de contactos
 * de riesgo.
 */
@AndroidEntryPoint
class StartRiskContactCheckReceiver : BroadcastReceiver() {

    /* Scope para las corrutinas */
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    /* Manager de alarmas de comprobación */
    @Inject lateinit var riskContactAlarmManager: RiskContactAlarmManager

    /* Referencia a la alarma de comproabación que disparó el Broadcast. */
    private var alarm: RiskContactAlarm? = null

    override fun onReceive(context: Context?, intent: Intent?) {

        /* Recuperar alarma de comprobación (Deserializar el ByteArray) */
        // No se puede usar Parcelables con AlarmManager porque no sabe construir el Parcelable.
        val byteArray = intent?.getByteArrayExtra(Constants.EXTRA_RISK_CONTACT_ALARM)
        byteArray?.let { alarm = AndroidUtils.toParcelable(byteArray, RiskContactAlarm.CREATOR) }

        alarm?.let { a ->
            /* Reprogramar la alarma para el siguiente día */
            resetAlarm(a)

            /* Iniciar Foreground Service para la comprobación */
            if(context != null)
                startCheckingService(context)
        }
    }

    /**
     * Reestablece la alarma para que sea ejecutada de nuevo al siguiente día
     * de la hora original a la que fue disparada.
     */
    private fun resetAlarm(alarm: RiskContactAlarm) {
        scope.launch {
            riskContactAlarmManager.reset(alarm)
        }
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