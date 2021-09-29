package es.uniovi.eii.contacttracker.managers

import android.app.AlarmManager
import android.content.Context
import es.uniovi.eii.contacttracker.location.alarms.LocationAlarm
import es.uniovi.eii.contacttracker.location.alarms.LocationAlarmManager
import es.uniovi.eii.contacttracker.model.Error
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.repositories.RiskContactRepository
import es.uniovi.eii.contacttracker.riskcontact.alarms.RiskContactAlarm
import es.uniovi.eii.contacttracker.riskcontact.alarms.RiskContactAlarmManager
import es.uniovi.eii.contacttracker.util.ValueWrapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.text.SimpleDateFormat

/**
 * Clase de pruebas Unitarias para el manager de alarmas de
 * comproabción de contactos de riesgo.
 */
@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class RiskContactAlarmManagerTest {

    /* Manager de alarmas de comprobación */
    private lateinit var manager: RiskContactAlarmManager
    /* Mocks */
    @Mock
    lateinit var alarmManager: AlarmManager
    @Mock
    lateinit var riskContactRepository: RiskContactRepository
    @Mock
    lateinit var ctx: Context

    /* Date formatter */
    private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    /* Alarmas de prueba */
    private val a1 = RiskContactAlarm(1, df.parse("28/09/2021 15:15:35")!!,true)
    private val a2 = RiskContactAlarm(2, df.parse("28/09/2021 15:15:35")!!,true)
    private val a3 = RiskContactAlarm(3, df.parse("28/09/2021 14:10:00")!!,true)
    private val a4 = RiskContactAlarm(4, df.parse("28/09/2021 18:00:00")!!,true)

    @Before
    fun setUp() {
        manager = RiskContactAlarmManager(
            alarmManager,
            riskContactRepository,
            ctx)
    }

    /* Código: RCAM1 */
    @Test
    fun `establecer alarma sin colisiones y sin superar el limite`() = runBlocking {
        // Mockear llamadas al repositorio
        `when`(riskContactRepository.getAlarmsBySetHour(df.parse("29/09/2021 15:15:00")!!)).thenReturn(listOf())
        `when`(riskContactRepository.getAlarms()).thenReturn(listOf())
        `when`(riskContactRepository.insertAlarm(a1)).thenReturn(a1.id)
        `when`(riskContactRepository.getAlarmById(a1.id!!)).thenReturn(null)

        val result = manager.set(a1)
        assertTrue(result is ValueWrapper.Success)
    }

    /* Código: RCAM2 */
    @Test
    fun `establecer alarma sin colisiones y sin superar el limite igualandolo`() = runBlocking {
        // Mockear llamadas al repositorio
        `when`(riskContactRepository.getAlarmsBySetHour(df.parse("29/09/2021 15:15:00")!!)).thenReturn(listOf())
        `when`(riskContactRepository.getAlarms()).thenReturn(listOf(a2, a3))
        `when`(riskContactRepository.insertAlarm(a1)).thenReturn(a1.id)
        `when`(riskContactRepository.getAlarmById(a1.id!!)).thenReturn(null)

        val result = manager.set(a1)
        assertTrue(result is ValueWrapper.Success)
    }

    /* Código: RCAM3 */
    @Test
    fun `establecer alarma sin colisiones y superando el limite sobrepasandolo`() = runBlocking {
        // Mockear llamadas al repositorio
        `when`(riskContactRepository.getAlarmsBySetHour(df.parse("29/09/2021 15:15:00")!!)).thenReturn(listOf())
        `when`(riskContactRepository.getAlarms()).thenReturn(listOf(a2, a3, a4))

        val result = manager.set(a1)
        assertTrue(result is ValueWrapper.Fail)
        assertEquals(Error.RISK_CONTACT_ALARM_COUNT_LIMIT_EXCEEDED, (result as ValueWrapper.Fail).error)
    }

    /* Código: RCAM4 */
    @Test
    fun `establecer alarma con colisiones`() = runBlocking {
        // Mockear llamadas al repositorio
        `when`(riskContactRepository.getAlarmsBySetHour(df.parse("29/09/2021 15:15:00")!!)).thenReturn(listOf(a2, a3))

        val result = manager.set(a1)
        assertTrue(result is ValueWrapper.Fail)
        assertEquals(Error.RISK_CONTACT_ALARM_COLLISION, (result as ValueWrapper.Fail).error)
    }
}