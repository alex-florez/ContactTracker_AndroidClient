package es.uniovi.eii.contacttracker.contacttrackerutils

import es.uniovi.eii.contacttracker.util.NumberUtils
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Clase de pruebas Unitarias para las utilidades con números.
 */
class NumberUtilsTest {

    /* Valor de prueba */
    private val n = 2.8776512

    @Test
    fun `redondear valor a un decimal`() {
        assertEquals(2.9, NumberUtils.round(n, 1), 0.01)
    }

    @Test
    fun `redondear valor a varios decimales`() {
        assertEquals(2.878, NumberUtils.round(n, 3), 0.01)
    }

    @Test
    fun `redondear valor a ningún decimal`() {
        assertEquals(3.0, NumberUtils.round(n, 0), 0.01)
    }

}