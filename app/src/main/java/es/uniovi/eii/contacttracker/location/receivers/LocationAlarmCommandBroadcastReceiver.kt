package es.uniovi.eii.contacttracker.location.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.location.services.LocationForegroundService

class LocationAlarmCommandBroadcastReceiver : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {ctx ->
            intent?.let {
                it.action?.let { action ->
                    Intent(ctx, LocationForegroundService::class.java).let { service ->
                        service.action = action
                        service.putExtra(Constants.EXTRA_COMMAND_FROM_ALARM, true)
                        ContextCompat.startForegroundService(ctx, service)
                    }
                }
            }
        }
    }
}