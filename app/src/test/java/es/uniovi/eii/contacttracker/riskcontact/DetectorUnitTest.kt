package es.uniovi.eii.contacttracker.riskcontact

import es.uniovi.eii.contacttracker.TestUtils.parseItinerary
import es.uniovi.eii.contacttracker.model.Itinerary
import es.uniovi.eii.contacttracker.model.Point
import es.uniovi.eii.contacttracker.model.RiskContactConfig
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
        val location = UserLocation(0, Point(43.532024, -5.912487, Date()), 0.0, "")
        var closest = detector.findClosestLocation(location, listOf())
        // Lista vacía
        assertEquals(null, closest.first)
        assertEquals(Double.POSITIVE_INFINITY, closest.second, 0.1)
        // Varias localizaciones
        val list = listOf(
            UserLocation(1, Point(43.5320505, -5.9124605, Date()), 0.0, ""),
            UserLocation(2, Point(43.5320376, -5.9123865, Date()), 0.0, ""),
            UserLocation(3, Point(43.532031, -5.9124588, Date()), 0.0, "")
        )
        closest = detector.findClosestLocation(location, list)
        assertEquals(3L, closest.first?.userlocationID)
        assertEquals(2.403, closest.second, 0.1)
    }

    /**
     * Test para comprobar el método que comprueba la coincidencia en el tiempo
     * entre dos localizaciones.
     */
    @Test
    fun `check time proximity`() {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val l1 = UserLocation(1, Point(43.5320505, -5.9124605,  df.parse("2021-06-20 12:05:30")), 0.0, "")
        val l2 = UserLocation(2, Point(43.5320376, -5.9123865,  df.parse("2021-06-20 12:06:17")), 0.0, "")
        val l3 = UserLocation(3, Point(43.5320376, -5.9123865, df.parse("2021-06-20 12:06:17")), 0.0, "")
        val l4 = UserLocation(4, Point(43.5320376, -5.9123865, df.parse("2021-06-20 12:15:20")), 0.0, "")
        val l5 = UserLocation(5, Point(43.5320376, -5.9123865,  df.parse("2021-06-20 11:59:20")),0.0, "")
        val l6 = UserLocation(6, Point(43.5320376, -5.9123865,  df.parse("2021-06-21 12:06:05")), 0.0, "")


        /* No hay coincidencia */
        assertEquals(false, detector.checkTimeProximity(l1,l4,5))
        assertEquals(false, detector.checkTimeProximity(l1,l5, 6))
        assertEquals(false, detector.checkTimeProximity(l1, l6, 5))
        /* Hay coincidencia */
        assertEquals(true, detector.checkTimeProximity(l1, l2, 5))
        assertEquals(true, detector.checkTimeProximity(l2, l3, 5))
        assertEquals(true, detector.checkTimeProximity(l1,l5, 7))
    }

    /**
     * Comprueba si el método de comprobar la coincidencia en el espacio
     * funciona correctamente.
     */
    @Test
    fun `check space proximity`(){
        val l1 = UserLocation(1, Point(43.532026, -5.912564, Date()), 0.0, "")
        val l2 = UserLocation(2, Point(43.532012, -5.912499, Date()),0.0, "")
        val l3 = UserLocation(3, Point(43.532022, -5.912544, Date()), 0.0, "")

        /* No hay coincidencia */
        assertEquals(false, detector.checkSpaceProximity(l1, l2,5.0))
        /* Hay coincidencia */
        assertEquals(true, detector.checkSpaceProximity(l1,l3,5.0))
    }

    // PRUEBAS UNITARIAS CON VARIOS ITINERARIOS
    // ****************************************
    @Test
    fun `empty itineraries`(){
        val positive = Itinerary(listOf(), "positive")
        val user = Itinerary(listOf(), "user")
        val result = detector.startChecking(user, positive)
        assertEquals(0, result.size)
    }

    /**
     * Test unitario con dos itinerarios que no generan contactos.
     */
    @Test
    fun `no contacts`(){
        val i1 = parseItinerary("itinerary1.txt")
        val i2 = parseItinerary("itinerary2.txt")
        val contacts = detector.startChecking(i1, i2)
        assertEquals(0, contacts.size)
    }

    /**
     * Test unitario con dos itinerarios que están cercanos en el tiempo
     * pero no en el espacio.
     */
    @Test
    fun `no contacts time proximity`(){
        val i1 = parseItinerary("itinerary1.txt")
        val i3 = parseItinerary("itinerary3.txt")
        val contacts = detector.startChecking(i1, i3)
        assertEquals(0, contacts.size)
    }

    /**
     * Test unitario con dos itinerarios que están cercanos en el
     * espacio pero no en el tiempo.
     */
    @Test
    fun `no contacts space proximity`(){
        val i2 = parseItinerary("itinerary2.txt")
        val i4 = parseItinerary("itinerary4.txt")
        val contacts = detector.startChecking(i2, i4)
        assertEquals(0, contacts.size)
    }

    /**
     * Test unitario con dos itinerarios que generan un contacto en
     * un solo día.
     */
    @Test
    fun `one contact one day`(){
        detector.setConfig(RiskContactConfig(
            securityDistanceMargin = 3.0,
            timeDifferenceMargin = 1
        ))
        val i1 = parseItinerary("itinerary1.txt")
        val i5 = parseItinerary("itinerary5.txt")
        val contacts = detector.startChecking(i1, i5)
        // 1 contacto
        assertEquals(1, contacts.size)
        assertEquals(3, contacts[0].contactLocations.size) // 3 puntos de contacto
        // Comprobar las localizaciones de contacto
        val locations = contacts[0].contactLocations
        // Localizaciones del positivo
        assertEquals("2", locations[0].positiveContactPointName)
        assertEquals("3", locations[1].positiveContactPointName)
        assertEquals("4", locations[2].positiveContactPointName)
        // Localizaciones del usuario
        assertEquals("8", locations[0].userContactPointName)
        assertEquals("9", locations[1].userContactPointName)
        assertEquals("10", locations[2].userContactPointName)
        // Comprobar propiedades del contacto
        assertEquals(15000L, contacts[0].exposeTime)
        assertEquals(1.766, contacts[0].meanProximity, 0.1)
        assertEquals(10000, contacts[0].meanTimeInterval)
        assertEquals(0.6134, contacts[0].riskScore, 0.1)
    }

    /**
     * Test unitario de dos itinerarios que generan varios
     * contactos en un mismo día.
     */
    @Test
    fun `multiple contacts one day`() {
        detector.setConfig(RiskContactConfig(
            securityDistanceMargin = 2.5,
            timeDifferenceMargin = 1
        ))
        val i1 = parseItinerary("itinerary1.txt")
        val i6 = parseItinerary("itinerary6.txt")
        val contacts = detector.startChecking(i1, i6)

        assertEquals(2, contacts.size)
        // Comprobación de las propiedades de los contactos
        val contact1 = contacts[0]
        val contact2 = contacts[1]
        assertEquals(3, contact1.contactLocations.size)
        assertEquals(2, contact2.contactLocations.size)
        // Localizaciones del Contacto 1
        assertEquals("3", contact1.contactLocations[0].userContactPointName)
        assertEquals("4", contact1.contactLocations[0].positiveContactPointName)
        assertEquals("4", contact1.contactLocations[1].userContactPointName)
        assertEquals("5", contact1.contactLocations[1].positiveContactPointName)
        assertEquals("5", contact1.contactLocations[2].userContactPointName)
        assertEquals("6", contact1.contactLocations[2].positiveContactPointName)
        // Localizaciones del Contacto 2
        assertEquals("8", contact2.contactLocations[0].userContactPointName)
        assertEquals("11", contact2.contactLocations[0].positiveContactPointName)
        assertEquals("9", contact2.contactLocations[1].userContactPointName)
        assertEquals("12", contact2.contactLocations[1].positiveContactPointName)
        // Propiedades del contacto 1
        assertEquals(10000, contact1.exposeTime)
        assertEquals(1.779, contact1.meanProximity, 0.1)
        assertEquals(10000, contact1.meanTimeInterval)
        assertEquals(0.61005, contact1.riskScore, 0.01)
        // Propiedades del contacto 2
        assertEquals(0, contact2.exposeTime)
        assertEquals(1.505, contact2.meanProximity, 0.1)
        assertEquals(10000, contact2.meanTimeInterval)
        assertEquals(0.6214, contact2.riskScore, 0.01)
    }

    /**
     * Test unitario con dos itinerarios de varios días
     * con varios contactos de riesgo.
     */
    @Test
    fun `multiple contacts multiple days`(){
        detector.setConfig(RiskContactConfig(
            securityDistanceMargin = 2.5,
            timeDifferenceMargin = 1
        ))
        val i7 = parseItinerary("itinerary7.txt")
        val i8 = parseItinerary("itinerary8.txt")
        val contacts = detector.startChecking(i7, i8)

        assertEquals(2, contacts.size)
        assertEquals(4, contacts[0].contactLocations.size)
        assertEquals(3, contacts[1].contactLocations.size)
        val contact1 = contacts[0]
        val contact2 = contacts[1]
        // Localizaciones del primer contacto
        assertEquals("5", contact1.contactLocations[0].userContactPointName)
        assertEquals("3", contact1.contactLocations[0].positiveContactPointName)
        assertEquals("6", contact1.contactLocations[1].userContactPointName)
        assertEquals("4", contact1.contactLocations[1].positiveContactPointName)
        assertEquals("7", contact1.contactLocations[2].userContactPointName)
        assertEquals("5", contact1.contactLocations[2].positiveContactPointName)
        assertEquals("8", contact1.contactLocations[3].userContactPointName)
        assertEquals("6", contact1.contactLocations[3].positiveContactPointName)
        // Localizaciones del segundo contacto
        assertEquals("10", contact2.contactLocations[0].userContactPointName)
        assertEquals("8", contact2.contactLocations[0].positiveContactPointName)
        assertEquals("11", contact2.contactLocations[1].userContactPointName)
        assertEquals("9", contact2.contactLocations[1].positiveContactPointName)
        assertEquals("12", contact2.contactLocations[2].userContactPointName)
        assertEquals("10", contact2.contactLocations[2].positiveContactPointName)
        // Comprobar propiedades de los contactos
        assertEquals(10000, contact1.exposeTime)
        assertEquals(0.7575, contact1.meanProximity, 0.1)
        assertEquals(10000, contact1.meanTimeInterval)
        assertEquals(0.662125, contact1.riskScore, 0.01)

        assertEquals(10000, contact2.exposeTime)
        assertEquals(0.7367, contact2.meanProximity, 0.1)
        assertEquals(10000, contact2.meanTimeInterval)
        assertEquals(0.6632, contact2.riskScore, 0.01)
    }
}