package es.uniovi.eii.contacttracker.riskcontact

import es.uniovi.eii.contacttracker.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Clase de pruebas Unitarias para la clase RiskContact.
 */
class RiskContactUnitTest {

    /* Contacto de riesgo de prueba */
    private lateinit var riskContact: RiskContact

    /* Date formatter */
    private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    @Before
    fun setUp(){
        /* Inicializar contacto de riesgo con configuración por defecto. */
        val config = RiskContactConfig(exposeTimeWeight = 0.4, meanProximityWeight = 0.4, meanTimeIntervalWeight = 0.2)
        riskContact = RiskContact(riskContactId = null, riskContactResultId = null, config = config)
    }

    /* Código: RC1 */
    @Test
    fun `agregar un punto de contacto de riesgo amarillo`() {
        riskContact.addContactLocations(
            UserLocation(11, Point(43.531779, -5.911516, df.parse("26/09/2021 12:00:00")!!), 0.0, ""),
            UserLocation(12, Point(43.531776, -5.911465, df.parse("26/09/2021 12:00:03")!!), 0.0,"")
        )
        assertNull(riskContact.startDate)
        assertNull(riskContact.endDate)
        assertEquals(0L, riskContact.exposeTime)
        assertEquals(4.12, riskContact.meanProximity, 0.01)
        assertEquals(0L, riskContact.meanTimeInterval)
        assertEquals(0.125, riskContact.riskScore, 0.01)
        assertEquals(12.5, riskContact.riskPercent, 0.01)
        assertEquals(RiskLevel.AMARILLO, riskContact.riskLevel)
    }

    /* Código: RC2 */
    @Test
    fun `agregar un punto de contacto de riesgo naranja`() {
        riskContact.addContactLocations(
            UserLocation(11, Point(43.531779, -5.911516, df.parse("26/09/2021 12:00:00")!!), 0.0, ""),
            UserLocation(12, Point(43.531774, -5.911489, df.parse("26/09/2021 12:00:03")!!), 0.0,"")
        )
        assertNull(riskContact.startDate)
        assertNull(riskContact.endDate)
        assertEquals(0L, riskContact.exposeTime)
        assertEquals(2.24, riskContact.meanProximity, 0.01)
        assertEquals(0L, riskContact.meanTimeInterval)
        assertEquals(0.25, riskContact.riskScore, 0.01)
        assertEquals(25.02, riskContact.riskPercent, 0.01)
        assertEquals(RiskLevel.NARANJA, riskContact.riskLevel)
    }

    /* Código: RC3 */
    @Test
    fun `agregar punto de contacto de riesgo rojo`() {
        // Modificar la configuración para que pueda alcanzar el nivel rojo
        riskContact.config = RiskContactConfig(
            exposeTimeWeight = 0.1, meanProximityWeight = 0.8, meanTimeIntervalWeight = 0.1, meanProximityRange = arrayOf(0.0, 15.0)
        )
        riskContact.addContactLocations(
            UserLocation(11, Point(43.531779, -5.911516, df.parse("26/09/2021 12:00:00")!!), 0.0, ""),
            UserLocation(12, Point(43.531777, -5.911506, df.parse("26/09/2021 12:00:03")!!), 0.0,"")
        )
        assertNull(riskContact.startDate)
        assertNull(riskContact.endDate)
        assertEquals(0L, riskContact.exposeTime)
        assertEquals(0.83, riskContact.meanProximity, 0.01)
        assertEquals(0L, riskContact.meanTimeInterval)
        assertEquals(0.755, riskContact.riskScore, 0.01)
        assertEquals(75.55, riskContact.riskPercent, 0.01)
        assertEquals(RiskLevel.ROJO, riskContact.riskLevel)
    }

    /* Código: RC4 */
    @Test
    fun `agregar 2 puntos de contacto con interseccion exacta entre fechas`() {
        riskContact.addContactLocations( // Punto de contacto 1
            UserLocation(11, Point(43.531779, -5.911516, df.parse("26/09/2021 12:00:00")!!), 0.0, ""),
            UserLocation(12, Point(43.531777, -5.911506, df.parse("26/09/2021 12:00:00")!!), 0.0,"")
        )
        riskContact.addContactLocations( // Punto de contacto 2
            UserLocation(21, Point(43.531751, -5.911550, df.parse("26/09/2021 12:00:05")!!), 0.0, ""),
            UserLocation(22, Point(43.531746, -5.911533, df.parse("26/09/2021 12:00:05")!!), 0.0,"")
        )
        // Comprobar inicio y fin del contacto de riesgo.
        assertNotNull(riskContact.startDate)
        assertNotNull(riskContact.endDate)
        val start = riskContact.startDate!!
        val end = riskContact.endDate!!
        assertEquals("26/09/2021 12:00:00", df.format(start))
        assertEquals("26/09/2021 12:00:05", df.format(end))

        assertEquals(5000L, riskContact.exposeTime)
        assertEquals(1.15, riskContact.meanProximity, 0.01)
        assertEquals(5000L, riskContact.meanTimeInterval)
        assertEquals(0.5238, riskContact.riskScore, 0.01)
        assertEquals(52.38, riskContact.riskPercent, 0.1)
        assertEquals(RiskLevel.NARANJA, riskContact.riskLevel)
    }


    /* Código: RC5 */
    @Test
    fun `agregar 2 puntos de contacto con interseccion interna entre fechas`() {
        riskContact.addContactLocations( // Punto de contacto 1
            UserLocation(11, Point(43.531779, -5.911516, df.parse("26/09/2021 12:00:00")!!), 0.0, ""),
            UserLocation(12, Point(43.531777, -5.911506, df.parse("26/09/2021 12:00:02")!!), 0.0,"")
        )
        riskContact.addContactLocations( // Punto de contacto 2
            UserLocation(21, Point(43.531751, -5.911550, df.parse("26/09/2021 12:00:05")!!), 0.0, ""),
            UserLocation(22, Point(43.531746, -5.911533, df.parse("26/09/2021 12:00:04")!!), 0.0,"")
        )
        // Comprobar inicio y fin del contacto de riesgo.
        assertNotNull(riskContact.startDate)
        assertNotNull(riskContact.endDate)
        val start = riskContact.startDate!!
        val end = riskContact.endDate!!
        assertEquals("26/09/2021 12:00:02", df.format(start))
        assertEquals("26/09/2021 12:00:04", df.format(end))

        assertEquals(2000L, riskContact.exposeTime)
        assertEquals(1.15, riskContact.meanProximity, 0.01)
        assertEquals(3500L, riskContact.meanTimeInterval)
        assertEquals(0.5230, riskContact.riskScore, 0.01)
        assertEquals(52.30, riskContact.riskPercent, 0.1)
        assertEquals(RiskLevel.NARANJA, riskContact.riskLevel)
    }

    /* Código: RC6 */
    @Test
    fun `agregar 2 puntos de contacto con interseccion externa entre fechas`() {
        riskContact.addContactLocations( // Punto de contacto 1
            UserLocation(11, Point(43.531779, -5.911516, df.parse("26/09/2021 12:00:00")!!), 0.0, ""),
            UserLocation(12, Point(43.531777, -5.911506, df.parse("26/09/2021 11:59:55")!!), 0.0,"")
        )
        riskContact.addContactLocations( // Punto de contacto 2
            UserLocation(21, Point(43.531751, -5.911550, df.parse("26/09/2021 12:00:05")!!), 0.0, ""),
            UserLocation(22, Point(43.531746, -5.911533, df.parse("26/09/2021 12:00:10")!!), 0.0,"")
        )
        // Comprobar inicio y fin del contacto de riesgo.
        assertNotNull(riskContact.startDate)
        assertNotNull(riskContact.endDate)
        val start = riskContact.startDate!!
        val end = riskContact.endDate!!
        assertEquals("26/09/2021 12:00:00", df.format(start))
        assertEquals("26/09/2021 12:00:05", df.format(end))

        assertEquals(5000L, riskContact.exposeTime)
        assertEquals(1.15, riskContact.meanProximity, 0.01)
        assertEquals(10000L, riskContact.meanTimeInterval)
        assertEquals(0.5222, riskContact.riskScore, 0.01)
        assertEquals(52.22, riskContact.riskPercent, 0.1)
        assertEquals(RiskLevel.NARANJA, riskContact.riskLevel)
    }

    /* Código: RC7 */
    @Test
    fun `agregar 2 puntos de contacto con interseccion derecha entre fechas`() {
        riskContact.addContactLocations( // Punto de contacto 1
            UserLocation(11, Point(43.531779, -5.911516, df.parse("26/09/2021 12:00:00")!!), 0.0, ""),
            UserLocation(12, Point(43.531777, -5.911506, df.parse("26/09/2021 12:00:03")!!), 0.0,"")
        )
        riskContact.addContactLocations( // Punto de contacto 2
            UserLocation(21, Point(43.531751, -5.911550, df.parse("26/09/2021 12:00:05")!!), 0.0, ""),
            UserLocation(22, Point(43.531746, -5.911533, df.parse("26/09/2021 12:00:10")!!), 0.0,"")
        )
        // Comprobar inicio y fin del contacto de riesgo.
        assertNotNull(riskContact.startDate)
        assertNotNull(riskContact.endDate)
        val start = riskContact.startDate!!
        val end = riskContact.endDate!!
        assertEquals("26/09/2021 12:00:03", df.format(start))
        assertEquals("26/09/2021 12:00:05", df.format(end))

        assertEquals(2000L, riskContact.exposeTime)
        assertEquals(1.15, riskContact.meanProximity, 0.01)
        assertEquals(6000L, riskContact.meanTimeInterval)
        assertEquals(0.5222, riskContact.riskScore, 0.01)
        assertEquals(52.22, riskContact.riskPercent, 0.1)
        assertEquals(RiskLevel.NARANJA, riskContact.riskLevel)
    }

    /* Código: RC8 */
    @Test
    fun `agregar 2 puntos de contacto con interseccion izquierda entre fechas`() {
        riskContact.addContactLocations( // Punto de contacto 1
            UserLocation(11, Point(43.531779, -5.911516, df.parse("26/09/2021 12:00:00")!!), 0.0, ""),
            UserLocation(12, Point(43.531777, -5.911506, df.parse("26/09/2021 11:59:55")!!), 0.0,"")
        )
        riskContact.addContactLocations( // Punto de contacto 2
            UserLocation(21, Point(43.531751, -5.911550, df.parse("26/09/2021 12:00:05")!!), 0.0, ""),
            UserLocation(22, Point(43.531746, -5.911533, df.parse("26/09/2021 12:00:03")!!), 0.0,"")
        )
        // Comprobar inicio y fin del contacto de riesgo.
        assertNotNull(riskContact.startDate)
        assertNotNull(riskContact.endDate)
        val start = riskContact.startDate!!
        val end = riskContact.endDate!!
        assertEquals("26/09/2021 12:00:00", df.format(start))
        assertEquals("26/09/2021 12:00:03", df.format(end))

        assertEquals(3000L, riskContact.exposeTime)
        assertEquals(1.15, riskContact.meanProximity, 0.01)
        assertEquals(6500L, riskContact.meanTimeInterval)
        assertEquals(0.5226, riskContact.riskScore, 0.01)
        assertEquals(52.26, riskContact.riskPercent, 0.1)
        assertEquals(RiskLevel.NARANJA, riskContact.riskLevel)
    }

    /* Código: RC9 */
    @Test
    fun `agregar 2 puntos de contacto que no generen interseccion`() {
        riskContact.addContactLocations( // Punto de contacto 1
            UserLocation(11, Point(43.531779, -5.911516, df.parse("26/09/2021 12:00:00")!!), 0.0, ""),
            UserLocation(12, Point(43.531777, -5.911506, df.parse("26/09/2021 11:59:50")!!), 0.0,"")
        )
        riskContact.addContactLocations( // Punto de contacto 2
            UserLocation(21, Point(43.531751, -5.911550, df.parse("26/09/2021 12:00:05")!!), 0.0, ""),
            UserLocation(22, Point(43.531746, -5.911533, df.parse("26/09/2021 11:59:55")!!), 0.0,"")
        )
        // Comprobar inicio y fin del contacto de riesgo.
        assertNull(riskContact.startDate)
        assertNull(riskContact.endDate)

        assertEquals(0L, riskContact.exposeTime)
        assertEquals(1.15, riskContact.meanProximity, 0.01)
        assertEquals(5000L, riskContact.meanTimeInterval)
        assertEquals(0.5216, riskContact.riskScore, 0.01)
        assertEquals(52.16, riskContact.riskPercent, 0.1)
        assertEquals(RiskLevel.NARANJA, riskContact.riskLevel)
    }

    /* Código: RC10 */
    @Test
    fun `agregar 3 puntos de contacto que generen una interseccion interna`() {
        riskContact.addContactLocations( // Punto de contacto 1
            UserLocation(11, Point(43.531779, -5.911516, df.parse("26/09/2021 12:00:00")!!), 0.0, ""),
            UserLocation(12, Point(43.531777, -5.911506, df.parse("26/09/2021 12:00:05")!!), 0.0,"")
        )
        riskContact.addContactLocations( // Punto de contacto 2
            UserLocation(21, Point(43.531751, -5.911550, df.parse("26/09/2021 12:08:00")!!), 0.0, ""),
            UserLocation(22, Point(43.531746, -5.911533, df.parse("26/09/2021 12:07:54")!!), 0.0,"")
        )
        riskContact.addContactLocations( // Punto de contacto 3
            UserLocation(31, Point(43.531732, -5.911567, df.parse("26/09/2021 12:14:00")!!), 0.0, ""),
            UserLocation(32, Point(43.531726, -5.911549, df.parse("26/09/2021 12:13:56")!!), 0.0,"")
        )
        // Comprobar inicio y fin del contacto de riesgo.
        assertNotNull(riskContact.startDate)
        assertNotNull(riskContact.endDate)

        val start = riskContact.startDate!!
        val end = riskContact.endDate!!
        assertEquals("26/09/2021 12:00:05", df.format(start))
        assertEquals("26/09/2021 12:13:56", df.format(end))

        assertEquals(831000L, riskContact.exposeTime)
        assertEquals(1.296, riskContact.meanProximity, 0.01)
        assertEquals(417750L, riskContact.meanTimeInterval)
        assertEquals(0.7436, riskContact.riskScore, 0.01)
        assertEquals(74.36, riskContact.riskPercent, 0.1)
        assertEquals(RiskLevel.NARANJA, riskContact.riskLevel)
    }

    /* Código: RC11 */
    @Test
    fun `agregar 3 puntos de contacto que generen una interseccion externa`() {
        riskContact.addContactLocations( // Punto de contacto 1
            UserLocation(11, Point(43.531779, -5.911516, df.parse("26/09/2021 12:00:00")!!), 0.0, ""),
            UserLocation(12, Point(43.531777, -5.911506, df.parse("26/09/2021 11:59:57")!!), 0.0,"")
        )
        riskContact.addContactLocations( // Punto de contacto 2
            UserLocation(21, Point(43.531751, -5.911550, df.parse("26/09/2021 12:08:00")!!), 0.0, ""),
            UserLocation(22, Point(43.531746, -5.911533, df.parse("26/09/2021 12:07:54")!!), 0.0,"")
        )
        riskContact.addContactLocations( // Punto de contacto 3
            UserLocation(31, Point(43.531732, -5.911567, df.parse("26/09/2021 12:14:00")!!), 0.0, ""),
            UserLocation(32, Point(43.531726, -5.911549, df.parse("26/09/2021 12:14:11")!!), 0.0,"")
        )
        // Comprobar inicio y fin del contacto de riesgo.
        assertNotNull(riskContact.startDate)
        assertNotNull(riskContact.endDate)

        val start = riskContact.startDate!!
        val end = riskContact.endDate!!
        assertEquals("26/09/2021 12:00:00", df.format(start))
        assertEquals("26/09/2021 12:14:00", df.format(end))

        assertEquals(840000L, riskContact.exposeTime)
        assertEquals(1.296, riskContact.meanProximity, 0.01)
        assertEquals(423500L, riskContact.meanTimeInterval)
        assertEquals(0.7457, riskContact.riskScore, 0.01)
        assertEquals(74.57, riskContact.riskPercent, 0.1)
        assertEquals(RiskLevel.NARANJA, riskContact.riskLevel)
    }

    /* Código: RC12 */
    @Test
    fun `agregar 3 puntos de contacto que generen una interseccion derecha`() {
        riskContact.addContactLocations( // Punto de contacto 1
            UserLocation(11, Point(43.531779, -5.911516, df.parse("26/09/2021 12:00:00")!!), 0.0, ""),
            UserLocation(12, Point(43.531777, -5.911506, df.parse("26/09/2021 12:00:05")!!), 0.0,"")
        )
        riskContact.addContactLocations( // Punto de contacto 2
            UserLocation(21, Point(43.531751, -5.911550, df.parse("26/09/2021 12:08:00")!!), 0.0, ""),
            UserLocation(22, Point(43.531746, -5.911533, df.parse("26/09/2021 12:07:54")!!), 0.0,"")
        )
        riskContact.addContactLocations( // Punto de contacto 3
            UserLocation(31, Point(43.531732, -5.911567, df.parse("26/09/2021 12:14:00")!!), 0.0, ""),
            UserLocation(32, Point(43.531726, -5.911549, df.parse("26/09/2021 12:14:11")!!), 0.0,"")
        )
        // Comprobar inicio y fin del contacto de riesgo.
        assertNotNull(riskContact.startDate)
        assertNotNull(riskContact.endDate)

        val start = riskContact.startDate!!
        val end = riskContact.endDate!!
        assertEquals("26/09/2021 12:00:05", df.format(start))
        assertEquals("26/09/2021 12:14:00", df.format(end))

        assertEquals(835000L, riskContact.exposeTime)
        assertEquals(1.296, riskContact.meanProximity, 0.01)
        assertEquals(421500L, riskContact.meanTimeInterval)
        assertEquals(0.7442, riskContact.riskScore, 0.01)
        assertEquals(74.42, riskContact.riskPercent, 0.1)
        assertEquals(RiskLevel.NARANJA, riskContact.riskLevel)
    }

    /* Código: RC13 */
    @Test
    fun `agregar 3 puntos de contacto que no generen interseccion`() {
        riskContact.addContactLocations( // Punto de contacto 1
            UserLocation(11, Point(43.531779, -5.911516, df.parse("26/09/2021 12:00:00")!!), 0.0, ""),
            UserLocation(12, Point(43.531777, -5.911506, df.parse("26/09/2021 12:14:45")!!), 0.0,"")
        )
        riskContact.addContactLocations( // Punto de contacto 2
            UserLocation(21, Point(43.531751, -5.911550, df.parse("26/09/2021 12:08:00")!!), 0.0, ""),
            UserLocation(22, Point(43.531746, -5.911533, df.parse("26/09/2021 12:16:00")!!), 0.0,"")
        )
        riskContact.addContactLocations( // Punto de contacto 3
            UserLocation(31, Point(43.531732, -5.911567, df.parse("26/09/2021 12:14:00")!!), 0.0, ""),
            UserLocation(32, Point(43.531726, -5.911549, df.parse("26/09/2021 12:17:10")!!), 0.0,"")
        )
        // Comprobar inicio y fin del contacto de riesgo.
        assertNull(riskContact.startDate)
        assertNull(riskContact.endDate)

        assertEquals(0L, riskContact.exposeTime)
        assertEquals(1.296, riskContact.meanProximity, 0.01)
        assertEquals(246250L, riskContact.meanTimeInterval)
        assertEquals(0.4315, riskContact.riskScore, 0.01)
        assertEquals(43.15, riskContact.riskPercent, 0.1)
        assertEquals(RiskLevel.NARANJA, riskContact.riskLevel)
    }
}