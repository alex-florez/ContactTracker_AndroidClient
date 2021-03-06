package es.uniovi.eii.contacttracker.util

import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import dagger.hilt.android.qualifiers.ApplicationContext
import es.uniovi.eii.contacttracker.activities.MainActivity
import es.uniovi.eii.contacttracker.location.services.LocationForegroundService
import es.uniovi.eii.contacttracker.model.UserLocation
import java.lang.Exception
import java.util.Date
import java.text.SimpleDateFormat

/**
 * Clase estática de utilidad, que contiene métodos de ayuda
 * para realizar operaciones básicas con la localización.
 */
object LocationUtils {

    /**
     * Código de solicitud de comprobación de ajustes de localización.
     */
    const val REQUEST_CHECK_LOCATION_SETTINGS: Int = 101

    /**
     * Formateador de fechas
     */
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    /**
     * Recibe como parámetro una petición de ubicación y comprueba
     * si las settings del dispositivo permiten llevar a cabo la petición
     * de localización.
     *
     * @param ctx Contexto de Android
     * @param locationRequest petición de localización.
     * @param listener listener para ser invocado tras completarse la operación con éxito.
     * @param activity Referencia a la actividad
     */
    fun checkLocationSettings(ctx:Context,
                              locationRequest: LocationRequest,
                              listener: OnSuccessListener<LocationSettingsResponse>,
                              activity: Activity) {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val result = LocationServices.getSettingsClient(ctx).checkLocationSettings(builder.build())
        result.addOnSuccessListener(listener)
                .addOnFailureListener {
                    when ((it as ApiException).statusCode){
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            val resolve = it as ResolvableApiException
                            resolve.startResolutionForResult(ctx as MainActivity, REQUEST_CHECK_LOCATION_SETTINGS)
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            // No es posible resolver el cambio de ajustes.
                        }
                    }
                }
    }

    /**
     * Método de utilidad que se encarga de comprobar si la ubicación del
     * dispositivo está activada.
     *
     * @param ctx Contexto de la app.
     * @return true si la ubicación está activada.
     */
    fun checkGPS(ctx: Context): Boolean {
        val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    /**
     * Instancia e inicializa un diálogo de Alerta para informar al usuario sobre la
     * configuración necesaria de los ajustes de la localización. Crea un intent para abrir
     * la activity de los ajustes de la app relativos a la localización.
     *
     * @param ctx Contexto de la app
     * @return diálogo de alerta.
     */
    fun createLocationSettingsAlertDialog(ctx: Context): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(ctx)
        dialogBuilder.setTitle("Ajustes de localización")
        dialogBuilder.setMessage("Para disfrutar de la aplicación es necesario que actives la ubicación del dispositivo.")
        // Botón de Aceptar
        dialogBuilder.setPositiveButton("Ajustes") { dialog, which ->
            val i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            ctx.startActivity(i)
        }
        // Botón de cancelar
        dialogBuilder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.cancel()
        }
        return dialogBuilder.create()
    }

    /**
     * Devuelve un String formateado con la información
     * de la Localización pasada como parámtro.
     *
     * @param location objeto Location de Android
     * @return string formateado
     */
    fun format(location: Location): String {
        return "Localización {Lat: ${location.latitude}, Lng: ${location.longitude}" +
                " Acc: ${location.accuracy}, Date: ${dateFormatter.format(Date(location.time))}}"
    }

    /**
     * Genera un String formateado a partir del objeto
     * de localización de usuario pasado como parámetro.
     */
    fun format(userLocation: UserLocation): String {
        return "Localización {ID: ${userLocation.id} Lat: ${userLocation.lat}, Lng: ${userLocation.lng}" +
                " Acc: ${userLocation.accuracy}, Date: ${dateFormatter.format(userLocation.locationTimestamp)}}"
    }

    /**
     * Se encarga de parsear un objeto Location del framework de
     * localización para convertirlo en un objeto UserLocation
     * del Dominio.
     */
    fun parse(location:Location): UserLocation {
        return UserLocation(
            null,
            location.latitude,
            location.longitude,
            location.accuracy.toDouble(),
            location.provider,
            Date(location.time)
        )
    }

    /**
     * Método que comprueba si hay algún servicio foreground de localización
     * ejecutándose en segundo plano.
     *
     * @return true si hay un servicio de localización ejecutándose.
     */
    fun isLocationServiceRunning(
        ctx: Context
    ): Boolean {
        val activityManager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for(service: ActivityManager.RunningServiceInfo in activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(LocationForegroundService::class.java.name.equals(service.service.className))
                return service.foreground
        }
        return false
    }

    /**
     * Recibe como parámetro una localización y crea un nuevo Intent
     * para mostrar la localización en un mapa de Google Maps.
     *
     * @param location localización a mostrar.
     */
    fun showLocationInMaps(
        ctx: Context,
        location: UserLocation,
        zoom: Int,
        label: String){
        val query = "${location.lat},${location.lng}(${label})"
        val uriString = "geo:${location.lat},${location.lng}?q=${query}&z=${zoom}"
        val uri = Uri.parse(uriString)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        ctx.startActivity(intent)
    }

}