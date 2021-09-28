package es.uniovi.eii.contacttracker.riskcontact

import es.uniovi.eii.contacttracker.TestUtils.parseItinerary
import es.uniovi.eii.contacttracker.model.*
import es.uniovi.eii.contacttracker.riskcontact.detector.RiskContactDetector
import es.uniovi.eii.contacttracker.riskcontact.detector.RiskContactDetectorImpl
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Clase de pruebas Unitarias para la implementación del algoritmo de detección
 * de contactos de riesgo entre dos itinerarios específicos de localizaciones.
 */
class DetectorUnitTest {

    /**
     * Detector de contactos.
     */
    private lateinit var detector: RiskContactDetector

    /* Date Formatter */
    private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    @Before
    fun setUp(){
        // Configuración
        val config = RiskContactConfig(
            securityDistanceMargin = 2.5, exposeTimeWeight = 0.4, meanProximityWeight = 0.4, meanTimeIntervalWeight = 0.2
        )
        detector = RiskContactDetectorImpl() // Instanciar el detector
        detector.setConfig(config)
    }

    /* Código: D12 */
    @Test
    fun `comprobar coincidencia en el tiempo`() {
        val l1 = UserLocation(1, Point(43.53155, -5.91184,  df.parse("20/06/2021 12:15:00")!!), 0.0, "")
        val l2 = UserLocation(2, Point(43.53153, -5.91187,  df.parse("20/06/2021 12:15:06")!!), 0.0, "")
        val l3 = UserLocation(3, Point(43.53151, -5.91187,  df.parse("20/06/2021 12:15:05")!!), 0.0, "")
        val l4 = UserLocation(4, Point(43.53151, -5.91191,  df.parse("20/06/2021 12:15:03")!!), 0.0, "")
        val l5 = UserLocation(5, Point(43.53148, -5.91191,  df.parse("18/06/2021 12:15:02")!!), 0.0, "")
        val l6 = UserLocation(6, Point(43.53148, -5.91191,  df.parse("20/06/2021 12:16:02")!!), 0.0, "")


        // Margen temporal
        val timeMargin = 5
        // Valores límite
        assertTrue(detector.checkTimeProximity(l1, l3, timeMargin))
        assertFalse(detector.checkTimeProximity(l1, l2, timeMargin))

        assertTrue(detector.checkTimeProximity(l1, l4, timeMargin))
        assertFalse(detector.checkTimeProximity(l1, l6, timeMargin))

        // Días distintos
        assertFalse(detector.checkTimeProximity(l1, l5, timeMargin))
    }

    /* Código: D34 */
    @Test
    fun `comprobar coincidencia en el espacio`() {
        val l1 = UserLocation(1, Point(43.53155, -5.91184,  df.parse("20/06/2021 12:15:00")!!), 0.0, "")
        val l2 = UserLocation(2, Point(43.53153, -5.91186,  df.parse("20/06/2021 12:15:06")!!), 0.0, "")
        val l3 = UserLocation(3, Point(43.53148, -5.91191,  df.parse("20/06/2021 12:15:05")!!), 0.0, "")
        val l4 = UserLocation(4, Point(43.53152, -5.91189,  df.parse("20/06/2021 12:15:03")!!), 0.0, "")
        val l5 = UserLocation(5, Point(43.53152, -5.91188,  df.parse("18/06/2021 12:15:02")!!), 0.0, "")

        // Margen de distancia de seguridad
        val distanceMargin = 5.0
        // Coinciden en el espacio
        assertTrue(detector.checkSpaceProximity(l1, l2, distanceMargin))
        // No coinciden
        assertFalse(detector.checkSpaceProximity(l1, l3, distanceMargin))
        // Valores límite
        assertFalse(detector.checkSpaceProximity(l1, l4, distanceMargin))
        assertTrue(detector.checkSpaceProximity(l1, l5, distanceMargin))
    }

    /* Código: D5 */
    @Test
    fun `buscar la localizacion mas cercana`() {
        // Localización objetivo
        val target = UserLocation(0, Point(43.53155, -5.91184, Date()), 0.0, "")
        // Lista de localizaciones vacía
        var closest = detector.findClosestLocation(target, listOf())
        assertNull(closest.first)
        assertEquals(Double.POSITIVE_INFINITY, closest.second, 0.01)
        // Lista con localizaciones
        val locations = listOf(
            UserLocation(1, Point(43.53152, -5.91188, Date()), 0.0, ""),
            UserLocation(2, Point(43.53148, -5.91184, Date()), 0.0, ""),
            UserLocation(3, Point(43.53149, -5.91191, Date()), 0.0, ""),
            UserLocation(4, Point(43.53153, -5.9118, Date()), 0.0, ""))
        closest = detector.findClosestLocation(target, locations)
        assertNotNull(closest.first)
        val closestLocation = closest.first!!
        assertEquals(4L, closestLocation.userlocationID)
        assertEquals(3.917, closest.second, 0.001)
    }

    // PRUEBAS UNITARIAS CON VARIOS ITINERARIOS
    // ****************************************

    /* Código: D6 */
    @Test
    fun `itinerarios con un solo dia que no coincide pero hay coincidencias en tiempo y espacio`() {
        val user = parseItinerary("itinerario1.txt")
        val positive = parseItinerary("itinerario2.txt")
        val result = detector.startChecking(user, positive)
        assertEquals(0, result.size)
    }

    /* Código: D7 */
    @Test
    fun `itinerarios con un solo dia que no coincide y tampoco hay coincidencias en tiempo y espacio`() {
        val user = parseItinerary("itinerario1.txt")
        val positive = parseItinerary("itinerario3.txt")
        val result = detector.startChecking(user, positive)
        assertEquals(0, result.size)
    }

    /* Código: D8 */
    @Test
    fun `itinerarios con el mismo dia y con varios puntos que coinciden solo en el tiempo`() {
        val user = parseItinerary("itinerario1.txt")
        val positive = parseItinerary("itinerario4.txt")
        val result = detector.startChecking(user, positive)
        assertEquals(0, result.size)
    }

    /* Código: D9 */
    @Test
    fun `itinerarios con el mismo dia y con varios puntos que coinciden solo en el espacio`() {
        val user = parseItinerary("itinerario1.txt")
        val positive = parseItinerary("itinerario5.txt")
        val result = detector.startChecking(user, positive)
        assertEquals(0, result.size)
    }

    /* Código: D10 */
    @Test
    fun `itinerarios con el mismo dia y con un punto de contacto entre dos localizaciones que coinciden en tiempo y espacio`() {
        val user = parseItinerary("itinerario1.txt")
        val positive = parseItinerary("itinerario6.txt")
        val result = detector.startChecking(user, positive)
        assertEquals(1, result.size)
        // Comprobar contacto de riesgo.
        val rc = result[0]
        // Puntos de contacto
        val contacts = rc.contactLocations
        assertEquals(1, contacts.size)
        assertEquals("7", contacts[0].userContactPointName)
        assertEquals("5", contacts[0].positiveContactPointName)
        // Fechas de inicio y de fin
        assertNull(rc.startDate)
        assertNull(rc.endDate)
        // Datos del contacto
        assertEquals(0L, rc.exposeTime)
        assertEquals(0.7655, rc.meanProximity, 0.001)
        assertEquals(0L, rc.meanTimeInterval)
        assertEquals(0.3489, rc.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc.riskLevel)
    }

    /* Código: D11 */
    @Test
    fun `itinerarios con el mismo dia con un contacto de riesgo de varios puntos cerrado porque no existen mas localizaciones del positivo`() {
        val user = parseItinerary("itinerario7.txt")
        val positive = parseItinerary("itinerario8.txt")
        val result = detector.startChecking(user, positive)
        assertEquals(1, result.size)
        // Comprobar contacto de riesgo.
        val rc = result[0]
        // Puntos de contacto
        val contacts = rc.contactLocations
        assertEquals(3, contacts.size)
        assertEquals("4", contacts[0].userContactPointName)
        assertEquals("3", contacts[0].positiveContactPointName)
        assertEquals("5", contacts[1].userContactPointName)
        assertEquals("4", contacts[1].positiveContactPointName)
        assertEquals("6", contacts[2].userContactPointName)
        assertEquals("5", contacts[2].positiveContactPointName)
        // Fechas de inicio y de fin
        assertEquals("20/06/2021 15:00:15", df.format(rc.startDate!!))
        assertEquals("20/06/2021 15:00:23", df.format(rc.endDate!!))
        // Datos del contacto
        assertEquals(8000L, rc.exposeTime)
        assertEquals(0.8267, rc.meanProximity, 0.001)
        assertEquals(5250L, rc.meanTimeInterval)
        assertEquals(0.5466, rc.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc.riskLevel)
    }

    /* Código: D12 */
    @Test
    fun `itinerarios con el mismo dia con un contacto de riesgo de varios puntos cerrado porque no existen mas localizaciones del usuario`() {
        val user = parseItinerary("itinerario7.txt")
        val positive = parseItinerary("itinerario9.txt")
        val result = detector.startChecking(user, positive)
        assertEquals(1, result.size)
        // Comprobar contacto de riesgo.
        val rc = result[0]
        // Puntos de contacto
        val contacts = rc.contactLocations
        assertEquals(3, contacts.size)
        assertEquals("21", contacts[0].userContactPointName)
        assertEquals("3", contacts[0].positiveContactPointName)
        assertEquals("22", contacts[1].userContactPointName)
        assertEquals("4", contacts[1].positiveContactPointName)
        assertEquals("23", contacts[2].userContactPointName)
        assertEquals("5", contacts[2].positiveContactPointName)
        // Fechas de inicio y de fin
        assertEquals("20/06/2021 15:01:40", df.format(rc.startDate!!))
        assertEquals("20/06/2021 15:01:47", df.format(rc.endDate!!))
        // Datos del contacto
        assertEquals(7000L, rc.exposeTime)
        assertEquals(0.8350, rc.meanProximity, 0.001)
        assertEquals(4750L, rc.meanTimeInterval)
        assertEquals(0.5458, rc.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc.riskLevel)
    }

    /* Código: D13 */
    @Test
    fun `itinerarios con el mismo dia con un contacto de riesgo de varios puntos cerrado porque ya no hay coincidencia en el tiempo y espacio`() {
        val user = parseItinerary("itinerario7.txt")
        val positive = parseItinerary("itinerario10.txt")
        val result = detector.startChecking(user, positive)
        assertEquals(1, result.size)
        // Comprobar contacto de riesgo.
        val rc = result[0]
        // Puntos de contacto
        val contacts = rc.contactLocations
        assertEquals(4, contacts.size)
        assertEquals("7", contacts[0].userContactPointName)
        assertEquals("4", contacts[0].positiveContactPointName)
        assertEquals("8", contacts[1].userContactPointName)
        assertEquals("5", contacts[1].positiveContactPointName)
        assertEquals("9", contacts[2].userContactPointName)
        assertEquals("6", contacts[2].positiveContactPointName)
        assertEquals("10", contacts[3].userContactPointName)
        assertEquals("7", contacts[3].positiveContactPointName)
        // Fechas de inicio y de fin
        assertEquals("20/06/2021 15:00:33", df.format(rc.startDate!!))
        assertEquals("20/06/2021 15:00:45", df.format(rc.endDate!!))
        // Datos del contacto
        assertEquals(12000L, rc.exposeTime)
        assertEquals(0.7909, rc.meanProximity, 0.001)
        assertEquals(4833L, rc.meanTimeInterval)
        assertEquals(0.5509, rc.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc.riskLevel)
    }

    /* Código: D14 */
    @Test
    fun `itinerarios con mismo dia, un contacto de riesgo de un solo punto y otro contacto de riesgo de varios puntos cerrado porque no hay coincidencia`() {
        val user = parseItinerary("itinerario7.txt")
        val positive = parseItinerary("itinerario11.txt")
        val result = detector.startChecking(user, positive)
        assertEquals(2, result.size)
        // Comprobar contacto de riesgo.
        val rc1 = result[0]
        val rc2 = result[1]
        // Puntos de contacto
        val contacts1 = rc1.contactLocations
        assertEquals(1, contacts1.size)
        assertEquals("5", contacts1[0].userContactPointName)
        assertEquals("4", contacts1[0].positiveContactPointName)
        // Fechas de inicio y de fin
        assertNull(rc1.startDate)
        assertNull(rc1.endDate)
        // Datos del contacto
        assertEquals(0L, rc1.exposeTime)
        assertEquals(0.5223, rc1.meanProximity, 0.001)
        assertEquals(0L, rc1.meanTimeInterval)
        assertEquals(0.36518, rc1.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc1.riskLevel)
        // Puntos de contacto
        val contacts2 = rc2.contactLocations
        assertEquals(3, contacts2.size)
        assertEquals("10", contacts2[0].userContactPointName)
        assertEquals("9", contacts2[0].positiveContactPointName)
        assertEquals("11", contacts2[1].userContactPointName)
        assertEquals("10", contacts2[1].positiveContactPointName)
        assertEquals("12", contacts2[2].userContactPointName)
        assertEquals("11", contacts2[2].positiveContactPointName)
        // Fechas de inicio y de fin
        assertEquals("20/06/2021 15:00:45", df.format(rc2.startDate!!))
        assertEquals("20/06/2021 15:00:54", df.format(rc2.endDate!!))

        // Datos del contacto
        assertEquals(9000L, rc2.exposeTime)
        assertEquals(0.73, rc2.meanProximity, 0.001)
        assertEquals(5000L, rc2.meanTimeInterval)
        assertEquals(0.5536, rc2.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc1.riskLevel)
    }

    /* Código: D15 */
    @Test
    fun `itinerarios con mismo dia, un contacto de riesgo de varios puntos cerrado porque no hay coincidencia y otro de varios puntos cerrado porque no hay mas localizaciones de usuario`() {
        val user = parseItinerary("itinerario7.txt")
        val positive = parseItinerary("itinerario12.txt")
        val result = detector.startChecking(user, positive)
        assertEquals(2, result.size)
        // Comprobar contacto de riesgo.
        val rc1 = result[0]
        val rc2 = result[1]
        // Puntos de contacto
        val contacts1 = rc1.contactLocations
        assertEquals(3, contacts1.size)
        assertEquals("15", contacts1[0].userContactPointName)
        assertEquals("4", contacts1[0].positiveContactPointName)
        assertEquals("16", contacts1[1].userContactPointName)
        assertEquals("5", contacts1[1].positiveContactPointName)
        assertEquals("17", contacts1[2].userContactPointName)
        assertEquals("6", contacts1[2].positiveContactPointName)
        // Fechas de inicio y de fin
        assertEquals("20/06/2021 15:01:10", df.format(rc1.startDate!!))
        assertEquals("20/06/2021 15:01:17", df.format(rc1.endDate!!))
        // Datos del contacto
        assertEquals(7000L, rc1.exposeTime)
        assertEquals(0.979, rc1.meanProximity, 0.001)
        assertEquals(5000L, rc1.meanTimeInterval)
        assertEquals(0.5361, rc1.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc1.riskLevel)
        // Puntos de contacto
        val contacts2 = rc2.contactLocations
        assertEquals(3, contacts2.size)
        assertEquals("21", contacts2[0].userContactPointName)
        assertEquals("13", contacts2[0].positiveContactPointName)
        assertEquals("22", contacts2[1].userContactPointName)
        assertEquals("14", contacts2[1].positiveContactPointName)
        assertEquals("23", contacts2[2].userContactPointName)
        assertEquals("15", contacts2[2].positiveContactPointName)
        // Fechas de inicio y de fin
        assertEquals("20/06/2021 15:01:43", df.format(rc2.startDate!!))
        assertEquals("20/06/2021 15:01:50", df.format(rc2.endDate!!))

        // Datos del contacto
        assertEquals(7000L, rc2.exposeTime)
        assertEquals(0.8350, rc2.meanProximity, 0.001)
        assertEquals(4250L, rc2.meanTimeInterval)
        assertEquals(0.5460, rc2.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc1.riskLevel)
    }

    /* Código: D16 */
    @Test
    fun `itinerarios con mismo dia, un contacto de riesgo de varios puntos cerrado porque no hay coincidencia y otro de varios puntos cerrado porque no hay más localizaciones de positivo`() {
        val user = parseItinerary("itinerario7.txt")
        val positive = parseItinerary("itinerario13.txt")
        val result = detector.startChecking(user, positive)
        assertEquals(2, result.size)
        // Comprobar contacto de riesgo.
        val rc1 = result[0]
        val rc2 = result[1]
        // Puntos de contacto
        val contacts1 = rc1.contactLocations
        assertEquals(3, contacts1.size)
        assertEquals("10", contacts1[0].userContactPointName)
        assertEquals("3", contacts1[0].positiveContactPointName)
        assertEquals("11", contacts1[1].userContactPointName)
        assertEquals("4", contacts1[1].positiveContactPointName)
        assertEquals("12", contacts1[2].userContactPointName)
        assertEquals("5", contacts1[2].positiveContactPointName)
        // Fechas de inicio y de fin
        assertEquals("20/06/2021 15:00:45", df.format(rc1.startDate!!))
        assertEquals("20/06/2021 15:00:52", df.format(rc1.endDate!!))
        // Datos del contacto
        assertEquals(7000L, rc1.exposeTime)
        assertEquals(0.7702, rc1.meanProximity, 0.001)
        assertEquals(4500, rc1.meanTimeInterval)
        assertEquals(0.5502, rc1.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc1.riskLevel)
        // Puntos de contacto
        val contacts2 = rc2.contactLocations
        assertEquals(3, contacts2.size)
        assertEquals("18", contacts2[0].userContactPointName)
        assertEquals("10", contacts2[0].positiveContactPointName)
        assertEquals("19", contacts2[1].userContactPointName)
        assertEquals("11", contacts2[1].positiveContactPointName)
        assertEquals("20", contacts2[2].userContactPointName)
        assertEquals("12", contacts2[2].positiveContactPointName)
        // Fechas de inicio y de fin
        assertEquals("20/06/2021 15:01:25", df.format(rc2.startDate!!))
        assertEquals("20/06/2021 15:01:32", df.format(rc2.endDate!!))

        // Datos del contacto
        assertEquals(7000L, rc2.exposeTime)
        assertEquals(0.5746, rc2.meanProximity, 0.001)
        assertEquals(5250L, rc2.meanTimeInterval)
        assertEquals(0.5630, rc2.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc1.riskLevel)
    }

    /* Código: D17 */
    @Test
    fun `itinerarios con mismo dia, un solo contacto de riesgo, una coincidencia en el tiempo y otra coincidencia en el espacio`() {
        val user = parseItinerary("itinerario4.txt")
        val positive = parseItinerary("itinerario14.txt")
        val result = detector.startChecking(user, positive)
        assertEquals(1, result.size)
        // Comprobar contacto de riesgo.
        val rc = result[0]
        // Puntos de contacto
        val contacts = rc.contactLocations
        assertEquals(3, contacts.size)
        assertEquals("2", contacts[0].userContactPointName)
        assertEquals("3", contacts[0].positiveContactPointName)
        assertEquals("3", contacts[1].userContactPointName)
        assertEquals("4", contacts[1].positiveContactPointName)
        assertEquals("4", contacts[2].userContactPointName)
        assertEquals("5", contacts[2].positiveContactPointName)
        // Fechas de inicio y de fin
        assertEquals("20/06/2021 10:00:06", df.format(rc.startDate!!))
        assertEquals("20/06/2021 10:00:14", df.format(rc.endDate!!))
        // Datos del contacto
        assertEquals(8000L, rc.exposeTime)
        assertEquals(0.7699, rc.meanProximity, 0.001)
        assertEquals(5000, rc.meanTimeInterval)
        assertEquals(0.5505, rc.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc.riskLevel)
    }

    /* Código: D18 */
    @Test
    fun `itinerario del usuario con un dia, itinerario del positivo con varios dias donde uno coincide con el del usuario`() {
        val user = parseItinerary("itinerario5.txt")
        val positive = parseItinerary("itinerario15.txt")
        val result = detector.startChecking(user, positive)
        assertEquals(1, result.size)
        // Comprobar contacto de riesgo.
        val rc = result[0]
        // Puntos de contacto
        val contacts = rc.contactLocations
        assertEquals(3, contacts.size)
        assertEquals("5", contacts[0].userContactPointName)
        assertEquals("10", contacts[0].positiveContactPointName)
        assertEquals("6", contacts[1].userContactPointName)
        assertEquals("11", contacts[1].positiveContactPointName)
        assertEquals("7", contacts[2].userContactPointName)
        assertEquals("12", contacts[2].positiveContactPointName)
        // Fechas de inicio y de fin
        assertEquals("20/06/2021 16:45:40", df.format(rc.startDate!!))
        assertEquals("20/06/2021 16:45:48", df.format(rc.endDate!!))
        // Datos del contacto
        assertEquals(8000L, rc.exposeTime)
        assertEquals(1.456, rc.meanProximity, 0.001)
        assertEquals(5000, rc.meanTimeInterval)
        assertEquals(0.5048, rc.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc.riskLevel)
    }

    /* Código: D19 */
    @Test
    fun `itinerario del usuario con varios dias, itinerario del positivo con un dia que coincide con uno del usuario`() {
        val user = parseItinerary("itinerario15.txt")
        val positive = parseItinerary("itinerario5.txt")
        val result = detector.startChecking(user, positive)
        assertEquals(1, result.size)
        // Comprobar contacto de riesgo.
        val rc = result[0]
        // Puntos de contacto
        val contacts = rc.contactLocations
        assertEquals(3, contacts.size)
        assertEquals("10", contacts[0].userContactPointName)
        assertEquals("5", contacts[0].positiveContactPointName)
        assertEquals("11", contacts[1].userContactPointName)
        assertEquals("6", contacts[1].positiveContactPointName)
        assertEquals("12", contacts[2].userContactPointName)
        assertEquals("7", contacts[2].positiveContactPointName)
        // Fechas de inicio y de fin
        assertEquals("20/06/2021 16:45:40", df.format(rc.startDate!!))
        assertEquals("20/06/2021 16:45:48", df.format(rc.endDate!!))
        // Datos del contacto
        assertEquals(8000L, rc.exposeTime)
        assertEquals(1.456, rc.meanProximity, 0.001)
        assertEquals(5000, rc.meanTimeInterval)
        assertEquals(0.5048, rc.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc.riskLevel)
    }

    /* Código: D20 */
    @Test
    fun `itinerarios con varios dias donde algunos coinciden y algunos dias tienen contactos de riesgo`() {
        val user = parseItinerary("itinerario16.txt")
        val positive = parseItinerary("itinerario17.txt")
        val result = detector.startChecking(user, positive)
        assertEquals(2, result.size)
        // Comprobar contacto de riesgo.
        val rc1 = result[0]
        val rc2 = result[1]
        // Puntos de contacto
        val contacts1 = rc1.contactLocations
        assertEquals(6, contacts1.size)
        assertEquals("17", contacts1[0].userContactPointName)
        assertEquals("16", contacts1[0].positiveContactPointName)

        assertEquals("18", contacts1[1].userContactPointName)
        assertEquals("17", contacts1[1].positiveContactPointName)

        assertEquals("19", contacts1[2].userContactPointName)
        assertEquals("18", contacts1[2].positiveContactPointName)

        assertEquals("20", contacts1[3].userContactPointName)
        assertEquals("19", contacts1[3].positiveContactPointName)

        assertEquals("21", contacts1[4].userContactPointName)
        assertEquals("20", contacts1[4].positiveContactPointName)

        assertEquals("22", contacts1[5].userContactPointName)
        assertEquals("21", contacts1[5].positiveContactPointName)

        // Fechas de inicio y de fin
        assertEquals("20/06/2021 11:20:10", df.format(rc1.startDate!!))
        assertEquals("20/06/2021 11:20:29", df.format(rc1.endDate!!))
        // Datos del contacto
        assertEquals(19000L, rc1.exposeTime)
        assertEquals(1.9415, rc1.meanProximity, 0.001)
        assertEquals(4000L, rc1.meanTimeInterval)
        assertEquals(0.4776, rc1.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc1.riskLevel)

        // Puntos de contacto
        val contacts2 = rc2.contactLocations
        assertEquals(3, contacts2.size)
        assertEquals("39", contacts2[0].userContactPointName)
        assertEquals("29", contacts2[0].positiveContactPointName)

        assertEquals("40", contacts2[1].userContactPointName)
        assertEquals("30", contacts2[1].positiveContactPointName)

        assertEquals("41", contacts2[2].userContactPointName)
        assertEquals("31", contacts2[2].positiveContactPointName)

        // Fechas de inicio y de fin
        assertEquals("22/06/2021 12:15:25", df.format(rc2.startDate!!))
        assertEquals("22/06/2021 12:15:33", df.format(rc2.endDate!!))
        // Datos del contacto
        assertEquals(8000L, rc2.exposeTime)
        assertEquals(1.643, rc2.meanProximity, 0.001)
        assertEquals(5000L, rc2.meanTimeInterval)
        assertEquals(0.4923, rc2.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc2.riskLevel)
    }

    /* Código: D21 */
    @Test
    fun `itinerarios con varios dias donde no coincide ninguno`() {
        val user = parseItinerary("itinerario16.txt")
        val positive = parseItinerary("itinerario18.txt")
        val result = detector.startChecking(user, positive)
        assertEquals(0, result.size)
    }

//
//    @Test
//    fun `empty itineraries`(){
//        val positive = Itinerary(listOf(), "positive")
//        val user = Itinerary(listOf(), "user")
//        val result = detector.startChecking(user, positive)
//        assertEquals(0, result.size)
//    }
//
//    /**
//     * Test unitario con dos itinerarios que no generan contactos.
//     */
//    @Test
//    fun `no contacts`(){
//        val i1 = parseItinerary("itinerary1.txt")
//        val i2 = parseItinerary("itinerary2.txt")
//        val contacts = detector.startChecking(i1, i2)
//        assertEquals(0, contacts.size)
//    }
//
//    /**
//     * Test unitario con dos itinerarios que están cercanos en el tiempo
//     * pero no en el espacio.
//     */
//    @Test
//    fun `no contacts time proximity`(){
//        val i1 = parseItinerary("itinerary1.txt")
//        val i3 = parseItinerary("itinerary3.txt")
//        val contacts = detector.startChecking(i1, i3)
//        assertEquals(0, contacts.size)
//    }
//
//    /**
//     * Test unitario con dos itinerarios que están cercanos en el
//     * espacio pero no en el tiempo.
//     */
//    @Test
//    fun `no contacts space proximity`(){
//        val i2 = parseItinerary("itinerary2.txt")
//        val i4 = parseItinerary("itinerary4.txt")
//        val contacts = detector.startChecking(i2, i4)
//        assertEquals(0, contacts.size)
//    }
//
//    /**
//     * Test unitario con dos itinerarios que generan un contacto en
//     * un solo día.
//     */
//    @Test
//    fun `one contact one day`(){
//        detector.setConfig(RiskContactConfig(
//            securityDistanceMargin = 3.0,
//            timeDifferenceMargin = 1
//        ))
//        val i1 = parseItinerary("itinerary1.txt")
//        val i5 = parseItinerary("itinerary5.txt")
//        val contacts = detector.startChecking(i1, i5)
//        // 1 contacto
//        assertEquals(1, contacts.size)
//        assertEquals(3, contacts[0].contactLocations.size) // 3 puntos de contacto
//        // Comprobar las localizaciones de contacto
//        val locations = contacts[0].contactLocations
//        // Localizaciones del positivo
//        assertEquals("2", locations[0].positiveContactPointName)
//        assertEquals("3", locations[1].positiveContactPointName)
//        assertEquals("4", locations[2].positiveContactPointName)
//        // Localizaciones del usuario
//        assertEquals("8", locations[0].userContactPointName)
//        assertEquals("9", locations[1].userContactPointName)
//        assertEquals("10", locations[2].userContactPointName)
//        // Comprobar propiedades del contacto
//        assertEquals(15000L, contacts[0].exposeTime)
//        assertEquals(1.766, contacts[0].meanProximity, 0.1)
//        assertEquals(10000, contacts[0].meanTimeInterval)
//        assertEquals(0.6134, contacts[0].riskScore, 0.1)
//    }
//
//    /**
//     * Test unitario de dos itinerarios que generan varios
//     * contactos en un mismo día.
//     */
//    @Test
//    fun `multiple contacts one day`() {
//        detector.setConfig(RiskContactConfig(
//            securityDistanceMargin = 2.5,
//            timeDifferenceMargin = 1
//        ))
//        val i1 = parseItinerary("itinerary1.txt")
//        val i6 = parseItinerary("itinerary6.txt")
//        val contacts = detector.startChecking(i1, i6)
//
//        assertEquals(2, contacts.size)
//        // Comprobación de las propiedades de los contactos
//        val contact1 = contacts[0]
//        val contact2 = contacts[1]
//        assertEquals(3, contact1.contactLocations.size)
//        assertEquals(2, contact2.contactLocations.size)
//        // Localizaciones del Contacto 1
//        assertEquals("3", contact1.contactLocations[0].userContactPointName)
//        assertEquals("4", contact1.contactLocations[0].positiveContactPointName)
//        assertEquals("4", contact1.contactLocations[1].userContactPointName)
//        assertEquals("5", contact1.contactLocations[1].positiveContactPointName)
//        assertEquals("5", contact1.contactLocations[2].userContactPointName)
//        assertEquals("6", contact1.contactLocations[2].positiveContactPointName)
//        // Localizaciones del Contacto 2
//        assertEquals("8", contact2.contactLocations[0].userContactPointName)
//        assertEquals("11", contact2.contactLocations[0].positiveContactPointName)
//        assertEquals("9", contact2.contactLocations[1].userContactPointName)
//        assertEquals("12", contact2.contactLocations[1].positiveContactPointName)
//        // Propiedades del contacto 1
//        assertEquals(10000, contact1.exposeTime)
//        assertEquals(1.779, contact1.meanProximity, 0.1)
//        assertEquals(10000, contact1.meanTimeInterval)
//        assertEquals(0.61005, contact1.riskScore, 0.01)
//        // Propiedades del contacto 2
//        assertEquals(0, contact2.exposeTime)
//        assertEquals(1.505, contact2.meanProximity, 0.1)
//        assertEquals(10000, contact2.meanTimeInterval)
//        assertEquals(0.6214, contact2.riskScore, 0.01)
//    }
//
//    /**
//     * Test unitario con dos itinerarios de varios días
//     * con varios contactos de riesgo.
//     */
//    @Test
//    fun `multiple contacts multiple days`(){
//        detector.setConfig(RiskContactConfig(
//            securityDistanceMargin = 2.5,
//            timeDifferenceMargin = 1
//        ))
//        val i7 = parseItinerary("itinerary7.txt")
//        val i8 = parseItinerary("itinerary8.txt")
//        val contacts = detector.startChecking(i7, i8)
//
//        assertEquals(2, contacts.size)
//        assertEquals(4, contacts[0].contactLocations.size)
//        assertEquals(3, contacts[1].contactLocations.size)
//        val contact1 = contacts[0]
//        val contact2 = contacts[1]
//        // Localizaciones del primer contacto
//        assertEquals("5", contact1.contactLocations[0].userContactPointName)
//        assertEquals("3", contact1.contactLocations[0].positiveContactPointName)
//        assertEquals("6", contact1.contactLocations[1].userContactPointName)
//        assertEquals("4", contact1.contactLocations[1].positiveContactPointName)
//        assertEquals("7", contact1.contactLocations[2].userContactPointName)
//        assertEquals("5", contact1.contactLocations[2].positiveContactPointName)
//        assertEquals("8", contact1.contactLocations[3].userContactPointName)
//        assertEquals("6", contact1.contactLocations[3].positiveContactPointName)
//        // Localizaciones del segundo contacto
//        assertEquals("10", contact2.contactLocations[0].userContactPointName)
//        assertEquals("8", contact2.contactLocations[0].positiveContactPointName)
//        assertEquals("11", contact2.contactLocations[1].userContactPointName)
//        assertEquals("9", contact2.contactLocations[1].positiveContactPointName)
//        assertEquals("12", contact2.contactLocations[2].userContactPointName)
//        assertEquals("10", contact2.contactLocations[2].positiveContactPointName)
//        // Comprobar propiedades de los contactos
//        assertEquals(10000, contact1.exposeTime)
//        assertEquals(0.7575, contact1.meanProximity, 0.1)
//        assertEquals(10000, contact1.meanTimeInterval)
//        assertEquals(0.662125, contact1.riskScore, 0.01)
//
//        assertEquals(10000, contact2.exposeTime)
//        assertEquals(0.7367, contact2.meanProximity, 0.1)
//        assertEquals(10000, contact2.meanTimeInterval)
//        assertEquals(0.6632, contact2.riskScore, 0.01)
//    }
}