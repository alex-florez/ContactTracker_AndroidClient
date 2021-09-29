package es.uniovi.eii.contacttracker.managers

import android.app.AlarmManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import es.uniovi.eii.contacttracker.location.alarms.LocationAlarm
import es.uniovi.eii.contacttracker.location.alarms.LocationAlarmManager
import es.uniovi.eii.contacttracker.model.Error
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.util.ValueWrapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import org.mockito.junit.MockitoJUnitRunner
import java.text.SimpleDateFormat


/**
 * Clase de pruebas Unitarias para el manager de alarmas de localización.
 */
@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class LocationAlarmManagerTest {


    /* Manager de alarmas de localización */
    private lateinit var manager: LocationAlarmManager
    /* Mocks */
    @Mock
    lateinit var alarmManager: AlarmManager
    @Mock
    lateinit var locationRepository: LocationRepository
    @Mock
    lateinit var ctx: Context

    /* Date formatter */
    private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    /* Alarmas de prueba */
    private val a1 = LocationAlarm(1, df.parse("28/09/2021 12:15:00")!!, df.parse("28/09/2021 12:15:00")!!,true)
    private val a2 = LocationAlarm(2, df.parse("28/09/2021 12:15:20")!!, df.parse("28/09/2021 12:13:00")!!,true)
    private val a3 = LocationAlarm(3, df.parse("28/09/2021 12:15:20")!!, df.parse("28/09/2021 12:20:00")!!,true)
    private val a4 = LocationAlarm(4, df.parse("28/09/2021 12:10:10")!!, df.parse("28/09/2021 12:25:00")!!,true)
    private val a5 = LocationAlarm(5, df.parse("28/09/2021 12:10:10")!!, df.parse("28/09/2021 12:18:00")!!,true)
    private val a6 = LocationAlarm(6, df.parse("28/09/2021 12:18:00")!!, df.parse("28/09/2021 12:25:00")!!,true)

    @Before
    fun setUp() {
        manager = LocationAlarmManager(
            alarmManager,
            locationRepository,
            ctx)
    }

    /* Código: LAM12 */
    @Test
    fun `alarmas invalidas`() = runBlocking {
        // Inicio == Fin
        var result = manager.setAlarm(a1)
        assertTrue(result is ValueWrapper.Fail)
        assertEquals(Error.INVALID_ALARM, (result as ValueWrapper.Fail).error)
        // Inicio > Fin
        result = manager.setAlarm(a2)
        assertTrue(result is ValueWrapper.Fail)
        assertEquals(Error.INVALID_ALARM, (result as ValueWrapper.Fail).error)
    }

    /* Código: LAM3 */
    @Test
    fun `alarma valida sin colisiones`() = runBlocking {
        // Mockear llamadas al repositorio
        `when`(locationRepository.getAlarmCollisions(a3)).thenReturn(listOf())
        `when`(locationRepository.insertLocationAlarm(a3)).thenReturn(a3.id)
        `when`(locationRepository.getAlarmByID(a3.id!!)).thenReturn(null)
        val result = manager.setAlarm(a3)
        assertTrue(result is ValueWrapper.Success)
    }

    /* Código: LAM4 */
    @Test
    fun `alarma valida que genera colision interna`() = runBlocking {
        // Mockear llamadas al repositorio
        `when`(locationRepository.getAlarmCollisions(a3)).thenReturn(listOf(a4))
        val result = manager.setAlarm(a3)
        assertTrue(result is ValueWrapper.Fail)
        assertEquals(Error.ALARM_COLLISION, (result as ValueWrapper.Fail).error)
    }

    /* Código: LAM5 */
    @Test
    fun `alarma valida que genera colision externa`() = runBlocking {
        // Mockear llamadas al repositorio
        `when`(locationRepository.getAlarmCollisions(a4)).thenReturn(listOf(a3))
        val result = manager.setAlarm(a4)
        assertTrue(result is ValueWrapper.Fail)
        assertEquals(Error.ALARM_COLLISION, (result as ValueWrapper.Fail).error)
    }

    /* Código: LAM6  */
    @Test
    fun `alarma valida que genera colision izquierda`() = runBlocking {
        // Mockear llamadas al repositorio
        `when`(locationRepository.getAlarmCollisions(a3)).thenReturn(listOf(a5))
        val result = manager.setAlarm(a3)
        assertTrue(result is ValueWrapper.Fail)
        assertEquals(Error.ALARM_COLLISION, (result as ValueWrapper.Fail).error)
    }

    /* Código: LAM7  */
    @Test
    fun `alarma valida que genera colision derecha`() = runBlocking {
        // Mockear llamadas al repositorio
        `when`(locationRepository.getAlarmCollisions(a3)).thenReturn(listOf(a6))
        val result = manager.setAlarm(a3)
        assertTrue(result is ValueWrapper.Fail)
        assertEquals(Error.ALARM_COLLISION, (result as ValueWrapper.Fail).error)
    }
}