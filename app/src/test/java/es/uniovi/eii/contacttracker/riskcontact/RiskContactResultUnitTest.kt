package es.uniovi.eii.contacttracker.riskcontact

import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.model.RiskContactResult
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat

/**
 * Pruebas Unitarias para la clase RiskContactResult que representa un resultado
 * de una comprobación de contactos de riesgo.
 */
class RiskContactResultUnitTest {

    /* Resultado de una comprobación */
    private lateinit var result: RiskContactResult

    /* Date Formatter */
    private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    @Before
    fun setUp() {
        result = RiskContactResult(
            1,
            mutableListOf(),
            1,
            df.parse("26/09/2021 12:00:00")!!
        )
    }

    /* Código: RCR1 */
    @Test
    fun `resultado con un solo contacto de riesgo`() {
        // Contacto de riesgo
        val rc = RiskContact(
            1,
            1,
            riskScore = 0.56,
            riskPercent = 56.0,
            exposeTime = 65000,
            meanProximity = 2.55,
            meanTimeInterval = 10000,
            startDate = df.parse("26/09/2021 10:00:00"),
            endDate = df.parse("26/09/2021 10:01:05")
        )
        result.riskContacts.add(rc)
        // Contacto de mayor riesgo
        val highestRiskContact = result.getHighestRiskContact()
        assertEquals(rc, highestRiskContact)
        // Medias
        val meanRisk = result.getTotalMeanRisk()
        val meanExposeTime = result.getTotalMeanExposeTime()
        val meanProximity = result.getTotalMeanProximity()
        assertEquals(56.0, meanRisk, 0.01)
        assertEquals(65000L, meanExposeTime)
        assertEquals(2.55, meanProximity, 0.01)

        // Contactos de riesgo ordenados por riesgo
        result.orderRiskContactsByRisk()
        assertEquals(rc, result.riskContacts[0])
    }

    /* Código: RCR2 */
    @Test
    fun `resultado con tres contactos de riesgo`() {
        // Contactos de riesgo
        val rc1 = RiskContact(
            1,
            1,
            riskScore = 0.56,
            riskPercent = 56.0,
            exposeTime = 65000,
            meanProximity = 2.55,
            meanTimeInterval = 10000,
            startDate = df.parse("26/09/2021 10:00:00"),
            endDate = df.parse("26/09/2021 10:01:05")
        )
        val rc2 = RiskContact(
            2,
            1,
            riskScore = 0.234,
            riskPercent = 23.4,
            exposeTime = 25400,
            meanProximity = 3.48,
            meanTimeInterval = 30000,
            startDate = df.parse("26/09/2021 10:14:00"),
            endDate = df.parse("26/09/2021 10:14:25")
        )
        val rc3 = RiskContact(
            3,
            1,
            riskScore = 0.856,
            riskPercent = 85.6,
            exposeTime = 105000,
            meanProximity = 1.28,
            meanTimeInterval = 10000,
            startDate = df.parse("26/09/2021 10:30:00"),
            endDate = df.parse("26/09/2021 10:31:45")
        )
        result.riskContacts.add(rc1)
        result.riskContacts.add(rc2)
        result.riskContacts.add(rc3)

        // Contacto de mayor riesgo
        val highestRiskContact = result.getHighestRiskContact()
        assertEquals(rc3, highestRiskContact)
        // Medias
        val meanRisk = result.getTotalMeanRisk()
        val meanExposeTime = result.getTotalMeanExposeTime()
        val meanProximity = result.getTotalMeanProximity()
        assertEquals(55.0, meanRisk, 0.01)
        assertEquals(65133L, meanExposeTime)
        assertEquals(2.436, meanProximity, 0.001)

        // Contactos de riesgo ordenados por riesgo
        result.orderRiskContactsByRisk()
        assertEquals(rc3, result.riskContacts[0])
        assertEquals(rc1, result.riskContacts[1])
        assertEquals(rc2, result.riskContacts[2])
    }
}