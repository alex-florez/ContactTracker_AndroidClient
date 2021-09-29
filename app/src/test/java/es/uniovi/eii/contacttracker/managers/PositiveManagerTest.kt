package es.uniovi.eii.contacttracker.managers

import es.uniovi.eii.contacttracker.fragments.dialogs.notifyquestions.ASYMPTOMATIC_QUESTION
import es.uniovi.eii.contacttracker.fragments.dialogs.notifyquestions.NotifyQuestionsDialog
import es.uniovi.eii.contacttracker.fragments.dialogs.notifyquestions.VACCINATED_QUESTION
import es.uniovi.eii.contacttracker.model.*
import es.uniovi.eii.contacttracker.network.model.APIResult
import es.uniovi.eii.contacttracker.network.model.ResponseError
import es.uniovi.eii.contacttracker.positive.NotifyPositiveResult
import es.uniovi.eii.contacttracker.positive.PositiveManager
import es.uniovi.eii.contacttracker.repositories.ConfigRepository
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.repositories.PositiveRepository
import es.uniovi.eii.contacttracker.util.ValueWrapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.text.SimpleDateFormat

/**
 * Clase de pruebas Unitarias para el manager de positivos.
 */
@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class PositiveManagerTest {

    /* Manager de positivos */
    private lateinit var manager: PositiveManager

    /* Mocks */
    @Mock
    lateinit var positiveRepository: PositiveRepository
    @Mock
    lateinit var locationRepository: LocationRepository
    @Mock
    lateinit var configRepository: ConfigRepository

    /* Date Formatter */
    private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    /* Datos de prueba */
    private val locations = listOf(
        UserLocation(1,Point(86.0, 120.0, df.parse("19/09/2021 12:54:45")!!), 0.0, ""),
        UserLocation(2,Point(86.556, 120.12, df.parse("19/09/2021 12:54:50")!!), 0.0, ""),
        UserLocation(3,Point(82.23, 121.2, df.parse("19/09/2021 13:30:12")!!), 0.0, ""),
        UserLocation(4,Point(20.2, 1000.45, df.parse("20/09/2021 11:10:11")!!), 0.0, ""),
        UserLocation(5,Point(21.22, 1000.67, df.parse("20/09/2021 11:10:20")!!), 0.0, ""))

    private val answers = mapOf(ASYMPTOMATIC_QUESTION to true, VACCINATED_QUESTION to false)
    private val now = df.parse("28/09/2021 12:21:50")!!
    private val config = NotifyPositiveConfig()
    private val notifiedPositive1 = Positive(1, "positivo1", df.parse("27/09/2021 11:16:10")!!)
    private val notifiedPositive2 = Positive(2, "positivo2", df.parse("26/09/2021 12:21:51")!!)
    private val notifiedPositive3 = Positive(3, "positivo3", df.parse("26/09/2021 11:05:00")!!)
    private val notifyResult = NotifyPositiveResult("código de positivo", 20)

    @Before
    fun setUp() = runBlockingTest {
        manager = PositiveManager(
            positiveRepository,
            locationRepository,
            configRepository)
        // Mocks
        `when`(configRepository.getNotifyPositiveConfig()).thenReturn(config)
    }

    /* Código: PM1 */
    @Test
    fun `limite de notificacion superado porque no han transcurrido suficientes dias`() = runBlockingTest {
        // Mocks
        `when`(configRepository.getNotifyPositiveConfig()).thenReturn(config)
        `when`(positiveRepository.getLastNotifiedPositive()).thenReturn(notifiedPositive1) // Probar 1 día transcurrido

        var result = manager.notifyPositive(null, answers, now)
        assertTrue(result is ValueWrapper.Fail)
        assertEquals(Error.NOTIFICATION_LIMIT_EXCEEDED, (result as ValueWrapper.Fail).error)

        // Probar casi dos días transcurridos (valor límite)
        `when`(positiveRepository.getLastNotifiedPositive()).thenReturn(notifiedPositive2)
        result = manager.notifyPositive(null, answers, now)
        assertTrue(result is ValueWrapper.Fail)
        assertEquals(Error.NOTIFICATION_LIMIT_EXCEEDED, (result as ValueWrapper.Fail).error)
    }

    /* Código: PM2 */
    @Test
    fun `notificar positivo sin superar el limite porque no existen otros positivos`() = runBlockingTest {
        // Mocks
        `when`(positiveRepository.getLastNotifiedPositive()).thenReturn(null)
        `when`(locationRepository.getLastLocationsSince(config.infectivityPeriod)).thenReturn(locations)
        `when`(positiveRepository.notifyPositive(anyObject())).thenReturn(APIResult.Success(notifyResult))

        val result = manager.notifyPositive(null, answers, now)
        assertTrue(result is ValueWrapper.Success)
        val success = result as ValueWrapper.Success
        assertEquals("código de positivo", success.value.positiveCode)
        assertEquals(20, success.value.uploadedLocations)
    }

    /* Código: PM3 */
    @Test
    fun `notificar positivo sin superar el limite porque han transcurrido varios dias pero no existen localizaciones`() = runBlockingTest {
        // Mocks
        `when`(positiveRepository.getLastNotifiedPositive()).thenReturn(notifiedPositive3)
        `when`(locationRepository.getLastLocationsSince(config.infectivityPeriod)).thenReturn(listOf())

        val result = manager.notifyPositive(null, answers, now)
        assertTrue(result is ValueWrapper.Fail)
        assertEquals(Error.NO_LOCATIONS_TO_NOTIFY, (result as ValueWrapper.Fail).error)
    }


    /* Código: PM4 */
    @Test
    fun `notificar positivo con exito sin superar el limite porque han transcurrido varios dias`() = runBlockingTest {
        // Mocks
        `when`(positiveRepository.getLastNotifiedPositive()).thenReturn(notifiedPositive3)
        `when`(locationRepository.getLastLocationsSince(config.infectivityPeriod)).thenReturn(locations)
        `when`(positiveRepository.notifyPositive(anyObject())).thenReturn(APIResult.Success(notifyResult))

        val result = manager.notifyPositive(null, answers, now)
        assertTrue(result is ValueWrapper.Success)
        val success = result as ValueWrapper.Success
        assertEquals("código de positivo", success.value.positiveCode)
        assertEquals(20, success.value.uploadedLocations)
    }

    /* Código: PM5 */
    @Test
    fun `notificar positivo con error de tiempo de espera agotado sin superar el limite porque han transcurrido varios dias`() = runBlockingTest {
        // Mocks
        `when`(positiveRepository.getLastNotifiedPositive()).thenReturn(notifiedPositive3)
        `when`(locationRepository.getLastLocationsSince(config.infectivityPeriod)).thenReturn(locations)
        `when`(positiveRepository.notifyPositive(anyObject())).thenReturn(APIResult.NetworkError)

        val result = manager.notifyPositive(null, answers, now)
        assertTrue(result is ValueWrapper.Fail)
        assertEquals(Error.TIMEOUT, (result as ValueWrapper.Fail).error)
    }

    /* Código: PM6 */
    @Test
    fun `notificar positivo con error http sin superar el limite porque han transcurrido varios dias`() = runBlockingTest {
        // Mocks
        `when`(positiveRepository.getLastNotifiedPositive()).thenReturn(notifiedPositive3)
        `when`(locationRepository.getLastLocationsSince(config.infectivityPeriod)).thenReturn(locations)
        `when`(positiveRepository.notifyPositive(anyObject())).thenReturn(APIResult.HttpError(403, ResponseError(403, "Esto es un error", 100)))

        val result = manager.notifyPositive(null, answers, now)
        assertTrue(result is ValueWrapper.Fail)
        assertEquals(Error.CANNOT_NOTIFY, (result as ValueWrapper.Fail).error)
    }
}

/* Código de ayuda para Mockito con Kotlin */
private fun <T> anyObject(): T {
    Mockito.anyObject<T>()
    return uninitialized()
}

private fun <T> uninitialized(): T = null as T