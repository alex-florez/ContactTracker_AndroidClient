package es.uniovi.eii.contacttracker.location

import es.uniovi.eii.contacttracker.location.alarms.LocationAlarm
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Clase de pruebas Unitarias para la clase LocationAlarm que representa
 * una alarma de localización.
 */
class LocationAlarmTest {

    /* Date formatter */
    private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    /* Fecha de prueba que simula la fecha actual */
    private lateinit var now: Date

    @Before
    fun setUp() {
        now = df.parse("28/09/2021 13:44:32")!!
    }

    /* Código: LA12 */
    @Test
    fun `alarmas invalidas`(){
        // Fechas iguales
        val a1 = LocationAlarm(null,
            df.parse("28/09/2021 12:15:04")!!,
            df.parse("28/09/2021 12:15:04")!!,
            true)
        assertFalse(a1.isValid())
        // Fecha de inicio posterior a la de fin
        val a2 = LocationAlarm(null,
            df.parse("28/09/2021 12:16:22")!!,
            df.parse("28/09/2021 12:15:04")!!,
            true)
        assertFalse(a2.isValid())
    }

    /* Código: LA3 */
    @Test
    fun `alarma valida actualizada`() {
        val a = LocationAlarm(null,
            df.parse("28/09/2021 13:45:20")!!,
            df.parse("28/09/2021 14:30:00")!!,
            true)
        assertTrue(a.isValid())
        a.updateHours(now)
        assertEquals("28/09/2021 13:45:20", df.format(a.startDate))
        assertEquals("28/09/2021 14:30:00", df.format(a.endDate))
        val start = a.getStartTime()
        val end = a.getEndTime()
        assertEquals(13, start[0])
        assertEquals(45, start[1])
        assertEquals(14, end[0])
        assertEquals(30, end[1])
    }

    /* Código: LA45 */
    @Test
    fun `alarma valida desfasada`(){
        // Fecha igual
        val a1 = LocationAlarm(null,
            df.parse("28/09/2021 13:44:31")!!,
            df.parse("28/09/2021 14:30:00")!!,
            true)
        assertTrue(a1.isValid())
        a1.updateHours(now)
        assertEquals("29/09/2021 13:44:31", df.format(a1.startDate))
        assertEquals("29/09/2021 14:30:00", df.format(a1.endDate))
        val start1 = a1.getStartTime()
        val end1 = a1.getEndTime()
        assertEquals(13, start1[0])
        assertEquals(44, start1[1])
        assertEquals(14, end1[0])
        assertEquals(30, end1[1])
        // Fecha de inicio anterior a la de prueba
        val a2 = LocationAlarm(null,
            df.parse("28/09/2021 10:15:21")!!,
            df.parse("28/09/2021 18:40:00")!!,
            true)
        assertTrue(a2.isValid())
        a2.updateHours(now)
        assertEquals("29/09/2021 10:15:21", df.format(a2.startDate))
        assertEquals("29/09/2021 18:40:00", df.format(a2.endDate))
        val start2 = a2.getStartTime()
        val end2 = a2.getEndTime()
        assertEquals(10, start2[0])
        assertEquals(15, start2[1])
        assertEquals(18, end2[0])
        assertEquals(40, end2[1])
    }

}