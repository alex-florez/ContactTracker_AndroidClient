package es.uniovi.eii.contacttracker.contacttrackerutils

import es.uniovi.eii.contacttracker.model.Point
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.util.LocationUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat

/**
 * Clase de pruebas Unitarias para las utilidades de localizaciones.
 */
class LocationUtilsTest {

    /* Date formatter */
    private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    /* Localización de prueba */
    private val location1 = UserLocation(1,
        Point(86.0, 120.0, df.parse("19/09/2021 12:54:45")!!), 20.0, "testing")

    /* Código: LU1 */
    @Test
    fun `formatear una localización de usuario`() {
        val formattedLocation = LocationUtils.format(location1)
        assertEquals("Localización {ID: 1 Lat: 86.0, Lng: 120.0 Acc: 20.0, Date: 19/09/2021 12:54:45}", formattedLocation)
    }

    /* Código: LU2 */
    @Test
    fun `convertir localización de usuario de latitud y longitud`() {
        val latLng = LocationUtils.toLatLng(location1)
        assertEquals(86.0, latLng.latitude, 0.01)
        assertEquals(120.0, latLng.longitude, 0.01)
    }

    /* Código: LU3 */
    @Test
    fun `obtener distancia entre dos puntos cercanos mediante Haversine`(){
        val date = df.parse("19/09/2021 12:15:05")!!
        val p1 = Point(43.531693, -5.911997, date)
        val p2 = Point(43.531630, -5.912039, date)
        assertEquals(7.74, LocationUtils.distance(p1, p2), 0.1)
    }

    /* Código: LU4 */
    @Test
    fun `obtener distancia entre dos puntos lejanos mediante Haversine`(){
        val date = df.parse("19/09/2021 12:15:05")!!
        val p1 = Point(43.531693, -5.911997, date)
        val p2 = Point(43.533698, -5.909762, date)
        assertEquals(286.66, LocationUtils.distance(p1, p2), 0.1)
    }
}