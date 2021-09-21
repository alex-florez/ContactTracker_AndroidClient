package es.uniovi.eii.contacttracker.contacttrackerutils

import es.uniovi.eii.contacttracker.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

/**
 * Clase de pruebas Unitarias para las utilidades de fechas.
 */
class DateUtilsTest {

    /* Date Format general */
    private val df = SimpleDateFormat("dd/MM/yyyy HH:mm")

    /* Fecha de prueba */
    private val date = df.parse("19/09/2021 11:20")!!
    private val otherDate = df.parse("19/09/2021 18:35")!!

    /* Código: DU1 */
    @Test
    fun `formatear fecha a dd-MM-yyyy HH-mm`() {
        val formatted = DateUtils.formatDate(date, "dd-MM-yyyy HH-mm")
        assertEquals("19-09-2021 11-20", formatted)
    }

    /* Código: DU2 */
    @Test
    fun `obtener fecha de horas y minutos`() {
        val hours = 12
        val mins = 29
        val date = DateUtils.getDate(hours, mins)
        val stringDate = SimpleDateFormat("HH:mm:ss").format(date)
        assertEquals("12:29:00", stringDate)
    }

    /* Código: DU3 */
    @Test
    fun `obtener fecha a partir de milisegundos`() {
        val millis = date.time
        val date = DateUtils.millisToDate(millis)
        assertEquals("19/09/2021 11:20", df.format(date))
    }

    /* Código: DU4 */
    @Test
    fun `obtener hora del día y minutos a partir de fecha`() {
        // Probar con la fecha 1
        var hourOfDay = DateUtils.getFromDate(date, Calendar.HOUR_OF_DAY)
        var mins = DateUtils.getFromDate(date, Calendar.MINUTE)
        assertEquals(11, hourOfDay)
        assertEquals(20, mins)
        // Probar con la fecha 2 con la hora de tarde
        hourOfDay = DateUtils.getFromDate(otherDate, Calendar.HOUR_OF_DAY)
        mins = DateUtils.getFromDate(otherDate, Calendar.MINUTE)
        assertEquals(18, hourOfDay)
        assertEquals(35, mins)
    }

    /* Código: DU5 */
    @Test
    fun `sumar días a una fecha`() {
        // Sumar un día
        var newDate = DateUtils.addToDate(date, Calendar.DATE, 1)
        assertEquals("20/09/2021 11:20", df.format(newDate))
        // Restar un día
        newDate = DateUtils.addToDate(date, Calendar.DATE, -1)
        assertEquals("18/09/2021 11:20", df.format(newDate))
    }

    /* Código: DU6 */
    @Test
    fun `sumar días a una fecha a final de mes`() {
        val dateAtTheEnd = df.parse("30/09/2021 16:30")!!
        var newDate = DateUtils.addToDate(dateAtTheEnd, Calendar.DATE, 1)
        assertEquals("01/10/2021 16:30", df.format(newDate))
        // Restar un día
        newDate = DateUtils.addToDate(newDate, Calendar.DATE, -1)
        assertEquals("30/09/2021 16:30", df.format(newDate))
    }

    /* Código: DU7 */
    @Test
    fun `sumar minutos a una fecha`() {
        // Sumar 45 minutos
        val newDate = DateUtils.addToDate(date, Calendar.MINUTE, 45)
        assertEquals("19/09/2021 12:05", df.format(newDate))
    }

    /* Código: DU8 */
    @Test
    fun `obtener minutos y segundos a partir de milisegundos sin llegar a completar un minuto`() {
        val millis = 54560L
        val minSecs = DateUtils.getMinuteSecond(millis)
        assertEquals(0, minSecs[0])
        assertEquals(54, minSecs[1])
    }

    /* Código: DU9 */
    @Test
    fun `obtener minutos y segundos a partir de milisegundos completando un minuto`() {
        val millis = 83489L
        val minSecs = DateUtils.getMinuteSecond(millis)
        assertEquals(1, minSecs[0])
        assertEquals(23, minSecs[1])
    }

    /* Código: DU10 */
    @Test
    fun `obtener minutos y segundos a partir de milisegundos completando varios minutos`() {
        val millis = 124402L
        val minSecs = DateUtils.getMinuteSecond(millis)
        assertEquals(2, minSecs[0])
        assertEquals(4, minSecs[1])
    }

    /* Código: DU11 */
    @Test
    fun `obtener milisegundos a partir de minutos y segundos sin completar un minuto`() {
        var millis = DateUtils.getMillis(0, 34)
        assertEquals(34000L, millis)
        // Probar valor límite de 59 segundos
        millis = DateUtils.getMillis(0, 59)
        assertEquals(59000L, millis)
    }

    /* Código: DU12 */
    @Test
    fun `obtener milisegundos a partir de minutos y segundos completando varios minutos`() {
        val millis = DateUtils.getMillis(2, 80)
        assertEquals(200000L, millis)
    }

    /* Código: DU13 */
    @Test
    fun `diferencia en segundos entre dos fechas iguales`() {
        val date2 = df.parse("19/09/2021 11:20")!!
        assertEquals(0, DateUtils.dateDifferenceInSecs(date, date2))
    }

    /* Código: DU14 */
    @Test
    fun `diferencia en segundos entre dos fechas con la misma hora y minutos`() {
        val date2 = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("19/09/2021 11:20:34")!!
        assertEquals(34, DateUtils.dateDifferenceInSecs(date, date2))
    }

    /* Código: DU15 */
    @Test
    fun `diferencia en segundos entre dos fechas distintas`() {
        val date2 = df.parse("19/09/2021 11:35")!!
        assertEquals(900, DateUtils.dateDifferenceInSecs(date, date2))
    }

    /* Código: DU16 */
    @Test
    fun `modificar año, mes y día de una fecha`() {
        val newYear = 2023
        val newMonth = 2
        val newDay = 15
        val newDate = DateUtils.modifyDate(date, newYear, newMonth, newDay)
        assertEquals("15/03/2023 11:20", df.format(newDate))
    }

    /* Código: DU17 */
    @Test
    fun `obtener días entre dos fechas del mismo día`() {
        val otherDate = df.parse("19/09/2021 12:23")!!
        assertEquals(0, DateUtils.getDaysBetweenDates(date, otherDate))
    }

    /* Código: DU18 */
    @Test
    fun `obtener días entre dos fechas de distintos días y misma hora`() {
        val otherDate = df.parse("15/09/2021 11:20")!!
        assertEquals(4, DateUtils.getDaysBetweenDates(date, otherDate))
    }

    /* Código: DU19 */
    @Test
    fun `obtener días entre dos fechas de distintos días y distinta hora`() {
        val otherDate = df.parse("15/09/2021 12:00")!!
        assertEquals(3, DateUtils.getDaysBetweenDates(date, otherDate))
    }

    /* Código: DU20 */
    @Test
    fun `obtener días entre dos fechas de distintos meses`() {
        val otherDate = df.parse("02/10/2021 12:00")!!
        assertEquals(13, DateUtils.getDaysBetweenDates(date, otherDate))
    }

}