package es.uniovi.eii.contacttracker.util

import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import es.uniovi.eii.contacttracker.Constants
import es.uniovi.eii.contacttracker.activities.MainActivity
import es.uniovi.eii.contacttracker.location.services.LocationForegroundService
import es.uniovi.eii.contacttracker.model.Point
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.util.NumberUtils.round
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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
     * Crea un diálogo de alerta que contiene un mensaje indicando la necesidad de
     * permitir el registro de ubicaciones en segundo plano para disfrutar de las
     * características de la aplicacion.
     *
     * @param ctx Contexto de la aplicación Android.
     * @param positiveCallback Callback para el botón de Aceptar.
     * @param dismissCallback Callback para el botón de Cancelar.
     */
    fun createBackgroundLocationAlertDialog(ctx: Context,
                                            positiveCallback: () -> Unit,
                                            dismissCallback: () -> Unit): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(ctx)
        dialogBuilder.setTitle("Localización en segundo plano")
        dialogBuilder.setMessage("Activa la ubicación en segundo plano para que tu localización se registre también mientras la aplicación no está activa")
        // Botón de Aceptar
        dialogBuilder.setPositiveButton("Ajustes de ubicación") { dialog, which ->
            positiveCallback()
        }
        // Botón de cancelar
        dialogBuilder.setNegativeButton("Cancelar") { dialog, which ->
            dismissCallback()
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
        return "Localización {ID: ${userLocation.userlocationID} Lat: ${userLocation.lat()}, Lng: ${userLocation.lng()}" +
                " Acc: ${userLocation.accuracy}, Date: ${dateFormatter.format(userLocation.timestamp())}}"
    }

    /**
     * Se encarga de parsear un objeto Location del framework de
     * localización para convertirlo en un objeto UserLocation
     * del Dominio.
     */
    fun parse(location:Location): UserLocation {
        val point = Point(
            location.latitude,
            location.longitude,
            Date(location.time)
        )
        return UserLocation(
            null,
            point,
            location.accuracy.toDouble(),
            location.provider
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
        val query = "${location.lat()},${location.lng()}(${label})"
        val uriString = "geo:${location.lat()},${location.lng()}?q=${query}&z=${zoom}"
        val uri = Uri.parse(uriString)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        ctx.startActivity(intent)
    }

    /**
     * Transforma la localización de usuario a un objeto
     * LatLng que entiende Google Maps.
     *
     * @param location localización de usuario.
     * @return LatLng con la localización convertida.
     */
    fun toLatLng(location: UserLocation): LatLng {
        return LatLng(location.lat(), location.lng())
    }

    /**
     * Convierte Grados a Radianes.
     */
    private fun toRadians(degrees: Double): Double{
        return degrees * (Math.PI / 180)
    }


    /**
     * Utiliza uno de los algoritmos típicos para calcular distancias entre
     * dos puntos en una figura esférica similar a la tierra, utiliza
     * la Fórmula Haversin para implementar Great-Circle distance
     * y devuelve la distancia aproximada en metros.
     * @param pointA Localización A con latitud y longitud.
     * @param pointB Localización B con latitud y longitud.
     * @return Distancia entre los puntos medida en METROS.
     */
    fun distance(pointA: UserLocation, pointB: UserLocation): Double {
        /* Haversin Formula */
        /* Considera que la tierra es una ESFERA y determina la GREAT-CIRCLE Distance */
        // Grados de diferencia entre latitudes y longitudes
        val latDiff = pointB.lat() - pointA.lat()
        val lngDiff = pointB.lng() - pointA.lng()
        // Convertir a radianes los grados de diferencia entre las latitudes y longitudes.
        val latDiffRads = toRadians(latDiff)
        val lngDiffRads = toRadians(lngDiff)
        // Calcular el término 'a' que está dentro de la raíz.
        val a = sin(latDiffRads/2) * sin(latDiffRads/2) +
                cos(toRadians(pointA.lat())) * cos(toRadians(pointB.lat())) *
                sin(lngDiffRads/2) * sin(lngDiffRads/2)
        // Aplicar arcotangente de dos parámetros y raíz cuadrada
        // Multiplicar por el radio de la tierra para obtener los km de distancia.
        val d = 2 * atan2(sqrt(a), sqrt(1-a)) * Constants.EARTH_RADIUS
        // Devolver distancia en metros y con 4 decimales
        return round(d*1000, 4)
    }

    /**
     * Utiliza la Haversine Formula para calcular la distancia entre dos puntos en
     * el mapa definidos por un Par de Latitud y Longitud.
     *
     * @param pointA Par con la latitud y longitud del Punto A.
     * @param pointB Par con la latitud y longitud del Punto B.
     * @return Distancia en metros.
     */
    fun distance(pointA: Pair<Double, Double>, pointB: Pair<Double, Double>): Double {
        /* Haversin Formula */
        /* Considera que la tierra es una ESFERA y determina la GREAT-CIRCLE Distance */
        // Grados de diferencia entre latitudes y longitudes
        val latDiff = pointB.first - pointA.first
        val lngDiff = pointB.second - pointA.second
        // Convertir a radianes los grados de diferencia entre las latitudes y longitudes.
        val latDiffRads = toRadians(latDiff)
        val lngDiffRads = toRadians(lngDiff)
        // Calcular el término 'a' que está dentro de la raíz.
        val a = sin(latDiffRads/2) * sin(latDiffRads/2) +
                cos(toRadians(pointA.first)) * cos(toRadians(pointB.first)) *
                sin(lngDiffRads/2) * sin(lngDiffRads/2)
        // Aplicar arcotangente de dos parámetros y raíz cuadrada
        // Multiplicar por el radio de la tierra para obtener los km de distancia.
        val d = 2 * atan2(sqrt(a), sqrt(1-a)) * Constants.EARTH_RADIUS
        // Devolver distancia en metros y con 4 decimales
        return round(d*1000, 4)
    }

    /**
     * Calcula la distancia en metros entre dos puntos indicados, utilizando
     * la fórmula de Haversine.
     */
    fun distance(pointA: Point, pointB: Point): Double {
        /* Haversin Formula */
        /* Considera que la tierra es una ESFERA y determina la GREAT-CIRCLE Distance */
        // Grados de diferencia entre latitudes y longitudes
        val latDiff = pointB.lat - pointA.lat
        val lngDiff = pointB.lng - pointA.lng
        // Convertir a radianes los grados de diferencia entre las latitudes y longitudes.
        val latDiffRads = toRadians(latDiff)
        val lngDiffRads = toRadians(lngDiff)
        // Calcular el término 'a' que está dentro de la raíz.
        val a = sin(latDiffRads/2) * sin(latDiffRads/2) +
                cos(toRadians(pointA.lat)) * cos(toRadians(pointB.lat)) *
                sin(lngDiffRads/2) * sin(lngDiffRads/2)
        // Aplicar arcotangente de dos parámetros y raíz cuadrada
        // Multiplicar por el radio de la tierra para obtener los km de distancia.
        val d = 2 * atan2(sqrt(a), sqrt(1-a)) * Constants.EARTH_RADIUS
        // Devolver distancia en metros y con 4 decimales
        return round(d*1000, 4)
    }

    /**
     * Parsea el fichero de texto de nombre indicado para obtener
     * una lista con las localizaciones de usuario que representa cada
     * línea del fichero. El fichero debe estar situado en Assets.
     *
     * @param filename Nombre del fichero de texto.
     * @return Lista con las localizaciones parseadas.
     */
    fun parseLocationsFile(filename: String, ctx: Context): List<UserLocation>{
        val lines = FileUtils.readFile(ctx, filename)
        val locations = mutableListOf<UserLocation>()
        var counter = 0
        lines.forEach {
            val data = it.split(",")
            val lat = data[1].toDouble()
            val lng = data[0].toDouble()
            val timestamp = DateUtils.toDate(data[2], "dd/MM/yyyy HH:mm:ss") ?: Date()
            // Fecha modificada.
            val date = DateUtils.modifyDate(timestamp, 2021, 8, 14)
            locations.add(
                UserLocation(
                    counter.toLong(),
                    Point(lat, lng, date),
                    0.0,
                    "file"
                )
            )
            counter++
        }
        return locations
    }

}