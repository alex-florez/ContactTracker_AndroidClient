package es.uniovi.eii.contacttracker.riskcontact

import es.uniovi.eii.contacttracker.model.Itinerary
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.riskcontact.detector.RiskContactDetector
import es.uniovi.eii.contacttracker.riskcontact.detector.RiskContactDetectorImpl
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Clase de pruebas unitarias para la implementación del
 * algoritmo de detección de contactos de riesgo entre
 * dos itinerarios.
 */
class DetectorUnitTest {

    /**
     * Detector de contactos.
     */
    private lateinit var detector: RiskContactDetector

    @Before
    fun setUp(){
        // Instanciar el detector
        detector = RiskContactDetectorImpl()
    }

    /**
     * Método para encontrar la localización más cercana a otra
     * de entre un conjunto de localizaciones.
     */
    @Test
    fun `find closest location`(){
        val location = UserLocation(0, 43.532024, -5.912487, 0.0, "", Date())
        var closest = detector.findClosestLocation(location, listOf())
        // Lista vacía
        assertEquals(null, closest.first)
        assertEquals(Double.POSITIVE_INFINITY, closest.second, 0.1)
        // Varias localizaciones
        val list = listOf(
            UserLocation(1, 43.5320505, -5.9124605, 0.0, "", Date()),
            UserLocation(2, 43.5320376, -5.9123865, 0.0, "", Date()),
            UserLocation(3, 43.532031, -5.9124588, 0.0, "", Date())
        )
        closest = detector.findClosestLocation(location, list)
        assertEquals(3L, closest.first?.id)
        assertEquals(2.403, closest.second, 0.1)
    }

    /**
     * Test para comprobar el método que comprueba la coincidencia en el tiempo
     * entre dos localizaciones.
     */
    @Test
    fun `check time proximity`() {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val l1 = UserLocation(1, 43.5320505, -5.9124605, 0.0, "", df.parse("2021-06-20 12:05:30"))
        val l2 = UserLocation(2, 43.5320376, -5.9123865, 0.0, "", df.parse("2021-06-20 12:06:17"))
        val l3 = UserLocation(3, 43.5320376, -5.9123865, 0.0, "", df.parse("2021-06-20 12:06:17"))
        val l4 = UserLocation(4, 43.5320376, -5.9123865, 0.0, "", df.parse("2021-06-20 12:15:20"))
        /* No hay coincidencia */
        assertEquals(false, detector.checkTimeProximity(l1,l4,5.0))
        /* Hay coincidencia */
        assertEquals(true, detector.checkTimeProximity(l1, l2, 5.0))
        assertEquals(true, detector.checkTimeProximity(l2, l3, 5.0))
    }

    /**
     * Comprueba si el método de comprobar la coincidencia en el espacio
     * funciona correctamente.
     */
    @Test
    fun `check space proximity`(){
        val l1 = UserLocation(1, 43.532026, -5.912564, 0.0, "", Date())
        val l2 = UserLocation(2, 43.532012, -5.912499, 0.0, "", Date())
        val l3 = UserLocation(3, 43.532022, -5.912544, 0.0, "", Date())

        /* No hay coincidencia */
        assertEquals(false, detector.checkSpaceProximity(l1, l2,5.0))
        /* Hay coincidencia */
        assertEquals(true, detector.checkSpaceProximity(l1,l3,5.0))
    }

    @Test
    fun `empty itineraries`(){
        val positive = Itinerary(mapOf())
        val user = Itinerary(mapOf())
        val result = detector.startChecking(user, positive)
        assertEquals(0, result.size)
    }

}