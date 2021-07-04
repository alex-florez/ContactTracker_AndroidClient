package es.uniovi.eii.contacttracker.riskcontact

import es.uniovi.eii.contacttracker.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Clase con tests unitarios para la clase RiskContact
 * que representa un contacto de riesgo con varias localizaciones
 * de contacto con un positivo.
 */
class RiskContactUnitTest {

    /* Referencia al contacto de riesgo de prueba */
    private lateinit var riskContact: RiskContact

    /* Date formatter*/
    private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    @Before
    fun setUp(){
        riskContact = RiskContact(riskContactId = null, riskContactResultId = null,
            config = RiskContactConfig(
                exposeTimeWeight = 0.4, meanProximityWeight = 0.45, meanTimeIntervalWeight = 0.15
            ) )
    }

    /**
     * Test unitario que comprueba el cálculo del tiempo de
     * exposición con una intersección exacta entre las horas
     * de las localizaciones.
     */
    @Test
    fun `check expose time exact intersection`(){
        /* Intersección exacta */
        riskContact.addContactLocations(
            UserLocation(11, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:00")), 0.0, ""),
            UserLocation(21, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:00")), 0.0, "")
        )
        riskContact.addContactLocations(
            UserLocation(12, Point(43.531779, -5.91148,  df.parse("21/06/2021 12:00:10")),0.0, ""),
            UserLocation(22, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:10")), 0.0, "")
        )
        riskContact.addContactLocations(
            UserLocation(13, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:20")),0.0, ""),
            UserLocation(23, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:20")), 0.0, "")
        )
        // Fecha de inicio y de fin
        assertEquals("21/06/2021 12:00:00", df.format(riskContact.startDate))
        assertEquals("21/06/2021 12:00:20", df.format(riskContact.endDate))
        // Tiempo de exposición
        assertEquals(20000, riskContact.exposeTime)
    }

    /**
     * Test unitario que comprueba el tiempo de exposición con una
     * intersección interna entre las fechas y horas de los puntos de contacto.
     */
    @Test
    fun `check expose time internal intersection`(){
        /* Intersección interna */
        riskContact.addContactLocations(
            UserLocation(11, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:00")), 0.0, ""),
            UserLocation(21, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:05")),0.0, "")
        )
        riskContact.addContactLocations(
            UserLocation(12, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:10")),0.0, ""),
            UserLocation(22, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:09")),0.0, "")
        )
        riskContact.addContactLocations(
            UserLocation(13, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:20")),0.0, ""),
            UserLocation(23, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:15")),0.0, "")
        )
        // Fecha de inicio y de fin
        assertEquals("21/06/2021 12:00:05", df.format(riskContact.startDate))
        assertEquals("21/06/2021 12:00:15", df.format(riskContact.endDate))
        // Tiempo de exposición
        assertEquals(10000, riskContact.exposeTime)
    }

    /**
     * Test Unitario que comprueba el cálculo del tiempo
     * de exposición con una intersección externa entre los timestamps
     * de las localizaciones.
     */
    @Test
    fun `check expose time external intersection`(){
        /* Intersección externa */
        riskContact.addContactLocations(
            UserLocation(11, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:00")), 0.0, ""),
            UserLocation(21, Point(43.531779, -5.91148, df.parse("21/06/2021 11:59:00")),0.0, "")
        )
        riskContact.addContactLocations(
            UserLocation(12, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:10")), 0.0, ""),
            UserLocation(22, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:12")),0.0, "")
        )
        riskContact.addContactLocations(
            UserLocation(13, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:20")),0.0, ""),
            UserLocation(23, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:25")),0.0, "")
        )
        // Fecha de inicio y de fin
        assertEquals("21/06/2021 12:00:00", df.format(riskContact.startDate))
        assertEquals("21/06/2021 12:00:20", df.format(riskContact.endDate))
        // Tiempo de exposición
        assertEquals(20000, riskContact.exposeTime)
    }

    /**
     * Comprueba el cálculo de tiempo de exposición
     * con una intersección por la izquierda.
     */
    @Test
    fun `check expose time left intersection`(){
        /* Intersección por la izquierda */
        riskContact.addContactLocations(
            UserLocation(11, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:00")), 0.0, ""),
            UserLocation(21, Point(43.531779, -5.91148, df.parse("21/06/2021 11:59:00")), 0.0, "")
        )
        riskContact.addContactLocations(
            UserLocation(12, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:10")),0.0, ""),
            UserLocation(22, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:12")), 0.0, "")
        )
        riskContact.addContactLocations(
            UserLocation(13, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:20")), 0.0, ""),
            UserLocation(23, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:15")), 0.0, "")
        )
        // Fecha de inicio y de fin
        assertEquals("21/06/2021 12:00:00", df.format(riskContact.startDate))
        assertEquals("21/06/2021 12:00:15", df.format(riskContact.endDate))
        // Tiempo de exposición
        assertEquals(15000, riskContact.exposeTime)
    }

    /**
     * Comprueba el cálculo del tiempo de exposición
     * con una intersección por la derecha.
     */
    @Test
    fun `check expose time right intersection`(){
        /* Intersección por la derecha */
        riskContact.addContactLocations(
            UserLocation(11, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:00")),0.0, ""),
            UserLocation(21, Point(43.531779, -5.91148,  df.parse("21/06/2021 12:00:05")), 0.0, "")
        )
        riskContact.addContactLocations(
            UserLocation(12, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:10")), 0.0, ""),
            UserLocation(22, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:12")),  0.0, "")
        )
        riskContact.addContactLocations(
            UserLocation(13, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:20")),0.0, ""),
            UserLocation(23, Point(43.531779, -5.91148, df.parse("21/06/2021 12:00:25")), 0.0, "")
        )
        // Fecha de inicio y de fin
        assertEquals("21/06/2021 12:00:05", df.format(riskContact.startDate))
        assertEquals("21/06/2021 12:00:20", df.format(riskContact.endDate))
        // Tiempo de exposición
        assertEquals(15000, riskContact.exposeTime)
    }

    /**
     * Comprueba el cálculo de la proximidad media.
     */
    @Test
    fun `check mean proximity`(){
        assertEquals(0.0, riskContact.meanProximity, 0.01)
        riskContact.addContactLocations(
            UserLocation(11, Point(43.53192309009996, -5.913243243698072, df.parse("21/06/2021 12:00:00")), 0.0, ""),
            UserLocation(21, Point(43.53191738405285, -5.913219581274731, df.parse("21/06/2021 12:00:05")), 0.0, "")
        )
        riskContact.addContactLocations(
            UserLocation(12, Point(43.53197138253921, -5.913188619181912, df.parse("21/06/2021 12:00:10")),  0.0, ""),
            UserLocation(22, Point(43.53196876782065, -5.913173105925772, df.parse("21/06/2021 12:00:12")),0.0, "")
        )
        riskContact.addContactLocations(
            UserLocation(13, Point(43.53202645322956, -5.913128095966302, df.parse("21/06/2021 12:00:20")), 0.0, ""),
            UserLocation(23, Point(43.53202431557776, -5.913113403953236, df.parse("21/06/2021 12:00:25")), 0.0, "")
        )
        assertEquals(1.5007, riskContact.meanProximity, 0.01)
    }

    /**
     * Comprueba el cálculo de la media de intervalo de tiempo
     * entre localizaciones.
     */
    @Test
    fun `check mean time interval`(){
        assertEquals(0, riskContact.meanTimeInterval)
        riskContact.addContactLocations(
            UserLocation(11, Point(43.53192309009996, -5.913243243698072,  df.parse("21/06/2021 11:59:34")), 0.0, ""),
            UserLocation(21, Point(43.53191738405285, -5.913219581274731, df.parse("21/06/2021 12:00:05")), 0.0, "")
        )
        riskContact.addContactLocations(
            UserLocation(12, Point(43.53197138253921, -5.913188619181912,  df.parse("21/06/2021 11:59:46")), 0.0, ""),
            UserLocation(22, Point(43.53196876782065, -5.913173105925772, df.parse("21/06/2021 12:00:22")),0.0, "")
        )
        riskContact.addContactLocations(
            UserLocation(13, Point(43.53202645322956, -5.913128095966302, df.parse("21/06/2021 12:00:28")), 0.0, ""),
            UserLocation(23, Point(43.53202431557776, -5.913113403953236, df.parse("21/06/2021 12:00:35")), 0.0, "")
        )
        assertEquals(21000, riskContact.meanTimeInterval)
    }

    /**
     * Comprueba el cálculo de la puntuación
     * de riesgo del contacto.
     */
    @Test
    fun `check risk score`(){
        riskContact.addContactLocations(
            UserLocation(11, Point(43.53192309009996, -5.913243243698072,  df.parse("21/06/2021 11:59:34")), 0.0, ""),
            UserLocation(21, Point(43.53191738405285, -5.913219581274731, df.parse("21/06/2021 12:00:05")), 0.0, "")
        )
        riskContact.addContactLocations(
            UserLocation(12, Point(43.53197138253921, -5.913188619181912, df.parse("21/06/2021 11:59:46")), 0.0, ""),
            UserLocation(22, Point(43.53196876782065, -5.913173105925772, df.parse("21/06/2021 12:00:22")), 0.0, "")
        )
        riskContact.addContactLocations(
            UserLocation(13, Point(43.53202645322956, -5.913128095966302, df.parse("21/06/2021 12:00:28")), 0.0, ""),
            UserLocation(23, Point(43.53202431557776, -5.913113403953236, df.parse("21/06/2021 12:00:35")), 0.0, "")
        )
        assertEquals(23000, riskContact.exposeTime)
        assertEquals(1.5007, riskContact.meanProximity, 0.01)
        assertEquals(21000, riskContact.meanTimeInterval)

        assertEquals(0.5374, riskContact.riskScore, 0.0001)
        assertEquals(53.74, riskContact.riskPercent, 0.01)
        assertEquals(RiskLevel.NARANJA, riskContact.riskLevel)
    }
}