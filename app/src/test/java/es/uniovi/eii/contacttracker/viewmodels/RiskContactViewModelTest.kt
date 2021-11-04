package es.uniovi.eii.contacttracker.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.TestCoroutineRule
import es.uniovi.eii.contacttracker.TestUtils
import es.uniovi.eii.contacttracker.alarms.AlarmHelper
import es.uniovi.eii.contacttracker.fragments.dialogs.notifyquestions.ASYMPTOMATIC_QUESTION
import es.uniovi.eii.contacttracker.fragments.dialogs.notifyquestions.VACCINATED_QUESTION
import es.uniovi.eii.contacttracker.getOrAwaitValue
import es.uniovi.eii.contacttracker.model.*
import es.uniovi.eii.contacttracker.network.api.PositiveAPI
import es.uniovi.eii.contacttracker.network.model.CheckResult
import es.uniovi.eii.contacttracker.notifications.InAppNotificationManager
import es.uniovi.eii.contacttracker.network.model.NotifyPositiveResponse
import es.uniovi.eii.contacttracker.repositories.*
import es.uniovi.eii.contacttracker.riskcontact.RiskContactManager
import es.uniovi.eii.contacttracker.riskcontact.alarms.RiskContactAlarmManager
import es.uniovi.eii.contacttracker.riskcontact.detector.RiskContactDetectorImpl
import es.uniovi.eii.contacttracker.room.AppDatabase
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException
import java.text.SimpleDateFormat

/**
 * Clase pruebas de Integración entre el ViewModel de notificación de positivos,
 * el manager de positivos y los repositorios correspondientes. Las llamadas a
 * la API REST se mockean con Mockito.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, manifest = "src/main/AndroidManifest.xml")
@HiltAndroidTest
@ExperimentalCoroutinesApi
class RiskContactViewModelTest {

    /* Reglas */
    @get:Rule
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule() // Para hacer funcionar los LiveData

    @get:Rule
    val testCoroutineRule = TestCoroutineRule() // Para utilizar el Scope de Test

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    /* ViewModels */
    private lateinit var riskContactVm: RiskContactViewModel
    private lateinit var resultsVm: RiskContactResultViewModel

    /* Mocks */
    @Mock
    lateinit var configRepository: ConfigRepository
    @Mock
    lateinit var positiveAPI: PositiveAPI
    @Mock
    lateinit var sharedPrefs: SharedPreferences
    @Mock
    lateinit var statisticsRepository: StatisticsRepository
    @Mock
    lateinit var inAppNotificationManager: InAppNotificationManager
    @Mock
    lateinit var alarmHelper: AlarmHelper
    @Captor
    lateinit var checkResultCaptor: ArgumentCaptor<CheckResult>

    /* Base de datos ROOM */
    private lateinit var db: AppDatabase

    /* Date Formatter */
    private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    /* Fecha que simula la fecha actual */
    private val now = df.parse("23/06/2021 12:45:00")!!

    /* Configuración de la comprobación de contactos */
    private val checkConfig = RiskContactConfig(
        securityDistanceMargin = 2.5, exposeTimeWeight = 0.4, meanProximityWeight = 0.4, meanTimeIntervalWeight = 0.2
    )
    /* Respuestas a las preguntas */
    private val answers = mapOf(ASYMPTOMATIC_QUESTION to true, VACCINATED_QUESTION to false)
    /* Datos personales */
    private val personalData = PersonalData(
        "71987655H", "Alex", "Flórez",
        "695 82 28 79", "Avilés", "33400")
    /* Respuesta a la notificación con éxito */
    private val notifyResponse = NotifyPositiveResponse("AFsEwtu7SD", 10)

    /* Listas de localizaciones */
    private lateinit var itinerary15: List<UserLocation>
    private lateinit var itinerary16: List<UserLocation>
    private lateinit var itinerary19: List<UserLocation>
    private lateinit var itinerary2: List<UserLocation>
    private lateinit var itinerary5: List<UserLocation>
    private lateinit var itinerary1: List<UserLocation>
    private lateinit var itinerary14: List<UserLocation>
    private lateinit var itinerary20: List<UserLocation>

    /* Repositorios reales */
    private lateinit var riskContactRepository: RiskContactRepository
    private lateinit var locationRepo: LocationRepository
    private lateinit var positiveRepo: PositiveRepository

    /* Spies */
    private lateinit var positiveRepoSpy: PositiveRepository

    @Before
    fun setUp(){
        // Contexto
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        // Base de datos ROOM en memoria
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .setTransactionExecutor(testCoroutineRule.testCoroutineDispatcher.asExecutor())
            .setQueryExecutor(testCoroutineRule.testCoroutineDispatcher.asExecutor())
            .allowMainThreadQueries()
            .build()
        // Construir repositorios
        riskContactRepository = RiskContactRepository(
            db.riskContactDao(), db.riskContactAlarmDao(), sharedPrefs, ctx)
        positiveRepo = PositiveRepository(
            positiveAPI, // Mock
            db.positiveDao(),
            testCoroutineRule.testCoroutineDispatcher) // En memoria
        positiveRepoSpy = spy(positiveRepo)

        locationRepo = LocationRepository(db.userLocationDao(), db.locationAlarmDao())

        // Construir el manager de alarmas de comprobación
        val am = RiskContactAlarmManager(
            riskContactRepository,
            alarmHelper)
        val rcm = RiskContactManager(
            RiskContactDetectorImpl(),
            locationRepo,
            positiveRepoSpy,
            riskContactRepository,
            configRepository,
            statisticsRepository,
            inAppNotificationManager)
        riskContactVm = RiskContactViewModel(rcm, am, testCoroutineRule.testCoroutineDispatcher)
        resultsVm = RiskContactResultViewModel(riskContactRepository)
        fillDB()
        initMocks()
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        db.close()
    }

    /* Código: RCVM1234 */
    @Test
    fun `insertar alarmas de comprobacion`() = testCoroutineRule.runBlockingTest {
        // Mocks
        `when`(alarmHelper.setRiskContactCheckAlarm(anyObject())).thenReturn(true)

        // Insertar alarma con éxito
        riskContactVm.addAlarm(df.parse("20/06/2021 14:55:34")!!)
        val result1 = riskContactVm.addAlarmSuccess.getOrAwaitValue()
        assertEquals("21/06/2021 14:55:00", df.format(result1.startDate)) // un día más porque estará desfasada
        verify(alarmHelper).setRiskContactCheckAlarm(result1) // Verificar que se invoca al Helper de alarmas Android

        // Insertar alarma que genera error de inserción
//        `when`(alarmHelper.setRiskContactCheckAlarm(anyObject())).thenReturn(false)
//        riskContactVm.addAlarm(df.parse("20/06/2021 13:13:13")!!)
//        val result2 = riskContactVm.addAlarmError.getOrAwaitValue()
//        assertEquals(R.string.genericError, result2)

        // Insertar alarma con éxito
        `when`(alarmHelper.setRiskContactCheckAlarm(anyObject())).thenReturn(true)
        riskContactVm.addAlarm(df.parse("20/06/2021 18:30:20")!!)
        val result3 = riskContactVm.addAlarmSuccess.getOrAwaitValue()
        assertEquals("21/06/2021 18:30:00", df.format(result3.startDate)) // un día más porque estará desfasada
        verify(alarmHelper).setRiskContactCheckAlarm(result3) // Verificar que se invoca al Helper de alarmas Android

        // Insertar alarma que genera colisión
        riskContactVm.addAlarm(df.parse("20/06/2021 18:30:10")!!)
        val result4 = riskContactVm.addAlarmError.getOrAwaitValue()
        assertEquals(R.string.checkAlarmErrorCollision, result4)


        // Insertar una tercera alarma
        riskContactVm.addAlarm(df.parse("20/06/2021 20:20:50")!!)
        val result5 = riskContactVm.addAlarmSuccess.getOrAwaitValue()
        assertEquals("21/06/2021 20:20:00", df.format(result5.startDate))
        verify(alarmHelper).setRiskContactCheckAlarm(result5) // Verificar que se invoca al Helper de alarmas Android

        // Insertar cuarta alarma superando el límite
        riskContactVm.addAlarm(df.parse("20/06/2021 09:55:05")!!)
        val result6 = riskContactVm.addAlarmError.getOrAwaitValue()
        assertEquals(R.string.checkAlarmErrorCollision, result6)

        // Comprobar alarmas almacenadas en la base de datos
        val alarms = riskContactRepository.getAlarms()
        assertEquals(3, alarms.size)
        assertEquals("21/06/2021 14:55:00", df.format(alarms[0].startDate))
        assertEquals("21/06/2021 18:30:00", df.format(alarms[1].startDate))
        assertEquals("21/06/2021 20:20:00", df.format(alarms[2].startDate))
    }


    /* Código: RCVM56 */
    @Test
    fun `activar y desactivar una alarma`() = testCoroutineRule.runBlockingTest {
        // Mocks
        `when`(alarmHelper.setRiskContactCheckAlarm(anyObject())).thenReturn(true)

        // Insertar alarma con éxito
        riskContactVm.addAlarm(df.parse("20/06/2021 14:55:34")!!)
        val result1 = riskContactVm.addAlarmSuccess.getOrAwaitValue()
        assertEquals("21/06/2021 14:55:00", df.format(result1.startDate)) // un día más porque estará desfasada
        assertTrue(result1.active)
        verify(alarmHelper).setRiskContactCheckAlarm(result1) // Verificar que se invoca al Helper de alarmas Android

        // Desactivarla
        riskContactVm.toggleCheckAlarms(false)
        var alarms = riskContactRepository.getAlarms()
        assertEquals(1, alarms.size)
        assertEquals("22/06/2021 14:55:00", df.format(alarms[0].startDate))
        assertFalse(alarms[0].active)

        // Activarla
        riskContactVm.toggleCheckAlarms(true)
        alarms = riskContactRepository.getAlarms()
        assertEquals(1, alarms.size)
        assertEquals("23/06/2021 14:55:00", df.format(alarms[0].startDate))
        assertTrue(alarms[0].active)
    }

    /* Código: RCVM7 */
    @Test
    fun `eliminar una alarma`() = testCoroutineRule.runBlockingTest {
        // Mocks
        `when`(alarmHelper.setRiskContactCheckAlarm(anyObject())).thenReturn(true)

        // Insertar alarma con éxito
        riskContactVm.addAlarm(df.parse("20/06/2021 14:55:34")!!)
        val result1 = riskContactVm.addAlarmSuccess.getOrAwaitValue()
        assertEquals("21/06/2021 14:55:00", df.format(result1.startDate)) // un día más porque estará desfasada
        assertTrue(result1.active)
        verify(alarmHelper).setRiskContactCheckAlarm(result1) // Verificar que se invoca al Helper de alarmas Android

        // Comprobar alarmas
        var alarms = riskContactRepository.getAlarms()
        assertEquals("21/06/2021 14:55:00", df.format(alarms[0].startDate))
        assertEquals(1L, alarms[0].id)

        // Eliminar alarma
        riskContactVm.removeAlarm(1L)
        alarms = riskContactRepository.getAlarms()
        assertTrue(alarms.isEmpty())
    }

    /* Código: RCVM8 */
    @Test
    fun `comprobacion con localizaciones del usuario fuera del alcance de la comprobacion`() = testCoroutineRule.runBlockingTest {
        // Mocks
        `when`(positiveAPI.getPositives(anyLong(), anyInt())).thenReturn(listOf())
        riskContactVm.startChecking(df.parse("28/06/2021 17:49:00")!!)

        verify(inAppNotificationManager).showRiskContactResultNotification(anyObject())
        verify(statisticsRepository).registerRiskContactResult(capture(checkResultCaptor))

        // Comprobar resultado
        val results = resultsVm.results.getOrAwaitValue()
        assertEquals(1, results.size)
        assertEquals(0, results[0].numberOfPositives)
        assertEquals(0, results[0].riskContacts.size)

        // Comprobar parámetros de la llamada a la API de Estadísticas
        assertEquals(0.0, checkResultCaptor.value.riskPercent, 0.01)
        assertEquals(0L, checkResultCaptor.value.exposeTime)
        assertEquals(0.0, checkResultCaptor.value.proximity, 0.01)
    }

    /* Código: RCVM9 */
    @Test
    fun `comprobacion con itinerario del usuario sin positivos dentro del alcance`() = testCoroutineRule.runBlockingTest {
        // Mocks
        `when`(positiveAPI.getPositives(anyLong(), anyInt())).thenReturn(listOf())
        riskContactVm.startChecking(now)
        verify(inAppNotificationManager).showRiskContactResultNotification(anyObject())
        verify(statisticsRepository).registerRiskContactResult(capture(checkResultCaptor))

        // Comprobar resultados almacenados en la base de datos
        val results = resultsVm.results.getOrAwaitValue()
        assertEquals(1, results.size)
        assertEquals(0, results[0].riskContacts.size)
        assertEquals(0, results[0].numberOfPositives)

        // Comprobar resultado que se envía a la nube
        assertEquals(0.0, checkResultCaptor.value.riskPercent, 0.01)
        assertEquals(0L, checkResultCaptor.value.exposeTime)
        assertEquals(0.0, checkResultCaptor.value.proximity, 0.01)
    }

    /* Código: RCVM10 */
    @Test
    fun `comprobacion con itinerario del usuario con un positivo con varios dias dentro del alcance sin contactos de riesgo`() = testCoroutineRule.runBlockingTest {
        // Positivos
        val positive = Positive(8, "positivo8-1", df.parse("23/06/2021 13:09:22")!!, itinerary16)
        // Mocks
        `when`(positiveAPI.getPositives(anyLong(), anyInt())).thenReturn(listOf(positive))

        riskContactVm.startChecking(now)
        verify(inAppNotificationManager).showRiskContactResultNotification(anyObject())
        verify(statisticsRepository).registerRiskContactResult(capture(checkResultCaptor))

        // Comprobar resultados almacenados en la base de datos
        val results = resultsVm.results.getOrAwaitValue()
        assertEquals(1, results.size)
        assertEquals(0, results[0].riskContacts.size)
        assertEquals(0, results[0].numberOfPositives)

        // Comprobar resultado que se envía a la nube
        assertEquals(0.0, checkResultCaptor.value.riskPercent, 0.01)
        assertEquals(0L, checkResultCaptor.value.exposeTime)
        assertEquals(0.0, checkResultCaptor.value.proximity, 0.01)
    }

    /* Código: RCVM11 */
    @Test
    fun `comprobacion con itinerario del usuario con un positivo con varios dias dentro del alcance con contactos de riesgo`() = testCoroutineRule.runBlockingTest {
        // Positivos
        val positive = Positive(9, "positivo9-1", df.parse("23/06/2021 13:09:22")!!, itinerary19)
        // Mocks
        `when`(positiveAPI.getPositives(anyLong(), anyInt())).thenReturn(listOf(positive))

        riskContactVm.startChecking(now)

        verify(inAppNotificationManager).showRiskContactResultNotification(anyObject())
        verify(statisticsRepository).registerRiskContactResult(capture(checkResultCaptor))

        // Comprobar resultados almacenados en la base de datos
        val results = resultsVm.results.getOrAwaitValue()
        assertEquals(1, results.size)
        assertEquals(2, results[0].riskContacts.size)
        assertEquals(1, results[0].numberOfPositives)
        // Comprobar contactos de riesgo
        val rc1 = results[0].riskContacts[0]
        val rc2 = results[0].riskContacts[1]
        // Puntos de contacto
        assertEquals(1L, rc1.riskContactId)
        assertEquals(4, rc1.contactLocations.size)
        assertEquals("10", rc1.contactLocations[0].userContactPointName)
        assertEquals("6", rc1.contactLocations[0].positiveContactPointName)
        assertEquals("11", rc1.contactLocations[1].userContactPointName)
        assertEquals("7", rc1.contactLocations[1].positiveContactPointName)
        assertEquals("12", rc1.contactLocations[2].userContactPointName)
        assertEquals("8", rc1.contactLocations[2].positiveContactPointName)
        assertEquals("13", rc1.contactLocations[3].userContactPointName)
        assertEquals("9", rc1.contactLocations[3].positiveContactPointName)
        // Fechas de inicio y de fin
        assertEquals("20/06/2021 16:45:38", df.format(rc1.startDate!!))
        assertEquals("20/06/2021 16:45:50", df.format(rc1.endDate!!))
        // Datos del contacto
        assertEquals(12000L, rc1.exposeTime)
        assertEquals(1.0106, rc1.meanProximity, 0.001)
        assertEquals(4833L, rc1.meanTimeInterval)
        assertEquals(0.5363, rc1.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc1.riskLevel)

        assertEquals(2L, rc2.riskContactId)
        assertEquals(1, rc2.contactLocations.size)
        assertEquals("14", rc2.contactLocations[0].userContactPointName)
        assertEquals("10", rc2.contactLocations[0].positiveContactPointName)
        // Fechas de inicio y de fin
        assertNull(rc2.startDate)
        assertNull(rc2.endDate)
        // Datos del contacto
        assertEquals(0L, rc2.exposeTime)
        assertEquals(0.8879, rc2.meanProximity, 0.001)
        assertEquals(0L, rc2.meanTimeInterval)
        assertEquals(0.3408, rc2.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc2.riskLevel)

        // Comprobar resultado que se envía a la nube
        assertEquals(43.855, checkResultCaptor.value.riskPercent, 0.001)
        assertEquals(6000L, checkResultCaptor.value.exposeTime)
        assertEquals(0.94925, checkResultCaptor.value.proximity, 0.001)
    }

    /* Código: RCVM12 */
    @Test
    fun `comprobacion con itinerario del usuario con varios positivos donde uno ha sido notificado por el propio usuario`() = testCoroutineRule.runBlockingTest {
        // Positivos
        val userPositive = Positive(10, "positivoUsuario10", df.parse("23/06/2021 13:09:22")!!, itinerary5)
        val p1 = Positive(11, "positive10-1", df.parse("23/06/2021 13:09:22")!!, itinerary19)

        // Insertar el Positivo del propio usuario en la base de datos
        positiveRepo.insertPositive(userPositive)

        // Mocks
        `when`(positiveAPI.getPositives(anyLong(), anyInt())).thenReturn(listOf(p1, userPositive))

        riskContactVm.startChecking(now)

        verify(inAppNotificationManager).showRiskContactResultNotification(anyObject())
        verify(statisticsRepository).registerRiskContactResult(capture(checkResultCaptor))

        // Comprobar resultados almacenados en la base de datos
        val results = resultsVm.results.getOrAwaitValue()
        assertEquals(1, results.size)
        assertEquals(2, results[0].riskContacts.size)
        assertEquals(1, results[0].numberOfPositives)
        // Comprobar contactos de riesgo
        val rc1 = results[0].riskContacts[0]
        val rc2 = results[0].riskContacts[1]
        // Puntos de contacto
        assertEquals(1L, rc1.riskContactId)
        assertEquals(4, rc1.contactLocations.size)
        assertEquals("10", rc1.contactLocations[0].userContactPointName)
        assertEquals("6", rc1.contactLocations[0].positiveContactPointName)
        assertEquals("11", rc1.contactLocations[1].userContactPointName)
        assertEquals("7", rc1.contactLocations[1].positiveContactPointName)
        assertEquals("12", rc1.contactLocations[2].userContactPointName)
        assertEquals("8", rc1.contactLocations[2].positiveContactPointName)
        assertEquals("13", rc1.contactLocations[3].userContactPointName)
        assertEquals("9", rc1.contactLocations[3].positiveContactPointName)
        // Fechas de inicio y de fin
        assertEquals("20/06/2021 16:45:38", df.format(rc1.startDate!!))
        assertEquals("20/06/2021 16:45:50", df.format(rc1.endDate!!))
        // Datos del contacto
        assertEquals(12000L, rc1.exposeTime)
        assertEquals(1.0106, rc1.meanProximity, 0.001)
        assertEquals(4833L, rc1.meanTimeInterval)
        assertEquals(0.5363, rc1.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc1.riskLevel)

        assertEquals(2L, rc2.riskContactId)
        assertEquals(1, rc2.contactLocations.size)
        assertEquals("14", rc2.contactLocations[0].userContactPointName)
        assertEquals("10", rc2.contactLocations[0].positiveContactPointName)
        // Fechas de inicio y de fin
        assertNull(rc2.startDate)
        assertNull(rc2.endDate)
        // Datos del contacto
        assertEquals(0L, rc2.exposeTime)
        assertEquals(0.8879, rc2.meanProximity, 0.001)
        assertEquals(0L, rc2.meanTimeInterval)
        assertEquals(0.3408, rc2.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc2.riskLevel)

        // Comprobar resultado que se envía a la nube
        assertEquals(43.855, checkResultCaptor.value.riskPercent, 0.001)
        assertEquals(6000L, checkResultCaptor.value.exposeTime)
        assertEquals(0.94925, checkResultCaptor.value.proximity, 0.001)
    }

    /* Código: RCVM13 */
    @Test
    fun `comprobacion con itinerario del usuario con varios positivos donde solo uno genera contactos`() = testCoroutineRule.runBlockingTest {
        // Positivos
        val p1 = Positive(11, "positive11-1", df.parse("23/06/2021 13:09:22")!!, itinerary19)
        val p2 = Positive(12, "positive11-2", df.parse("22/06/2021 13:09:22")!!, itinerary14)
        val p3 = Positive(13, "positive11-3", df.parse("23/06/2021 12:30:00")!!, itinerary1)

        // Mocks
        `when`(positiveAPI.getPositives(anyLong(), anyInt())).thenReturn(listOf(p1, p2, p3))

        riskContactVm.startChecking(now)

        verify(inAppNotificationManager).showRiskContactResultNotification(anyObject())
        verify(statisticsRepository).registerRiskContactResult(capture(checkResultCaptor))

        // Comprobar resultados almacenados en la base de datos
        val results = resultsVm.results.getOrAwaitValue()
        assertEquals(1, results.size)
        assertEquals(2, results[0].riskContacts.size)
        assertEquals(1, results[0].numberOfPositives)
        // Comprobar contactos de riesgo
        val rc1 = results[0].riskContacts[0]
        val rc2 = results[0].riskContacts[1]
        assertEquals("Positivo 1", rc1.positiveLabel)
        assertEquals("Positivo 1", rc2.positiveLabel)

        // Puntos de contacto
        assertEquals(1L, rc1.riskContactId)
        assertEquals(4, rc1.contactLocations.size)
        assertEquals("10", rc1.contactLocations[0].userContactPointName)
        assertEquals("6", rc1.contactLocations[0].positiveContactPointName)
        assertEquals("11", rc1.contactLocations[1].userContactPointName)
        assertEquals("7", rc1.contactLocations[1].positiveContactPointName)
        assertEquals("12", rc1.contactLocations[2].userContactPointName)
        assertEquals("8", rc1.contactLocations[2].positiveContactPointName)
        assertEquals("13", rc1.contactLocations[3].userContactPointName)
        assertEquals("9", rc1.contactLocations[3].positiveContactPointName)
        // Fechas de inicio y de fin
        assertEquals("20/06/2021 16:45:38", df.format(rc1.startDate!!))
        assertEquals("20/06/2021 16:45:50", df.format(rc1.endDate!!))
        // Datos del contacto
        assertEquals(12000L, rc1.exposeTime)
        assertEquals(1.0106, rc1.meanProximity, 0.001)
        assertEquals(4833L, rc1.meanTimeInterval)
        assertEquals(0.5363, rc1.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc1.riskLevel)

        assertEquals(2L, rc2.riskContactId)
        assertEquals(1, rc2.contactLocations.size)
        assertEquals("14", rc2.contactLocations[0].userContactPointName)
        assertEquals("10", rc2.contactLocations[0].positiveContactPointName)
        // Fechas de inicio y de fin
        assertNull(rc2.startDate)
        assertNull(rc2.endDate)
        // Datos del contacto
        assertEquals(0L, rc2.exposeTime)
        assertEquals(0.8879, rc2.meanProximity, 0.001)
        assertEquals(0L, rc2.meanTimeInterval)
        assertEquals(0.3408, rc2.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc2.riskLevel)

        // Comprobar resultado que se envía a la nube
        assertEquals(43.855, checkResultCaptor.value.riskPercent, 0.001)
        assertEquals(6000L, checkResultCaptor.value.exposeTime)
        assertEquals(0.94925, checkResultCaptor.value.proximity, 0.001)
    }

    /* Código: RCVM14 */
    @Test
    fun `comprobacion con itinerario del usuario con varios positivos que generan contactos de riesgo`() = testCoroutineRule.runBlockingTest {
        // Positivos
        val p1 = Positive(12, "positive12-1", df.parse("23/06/2021 13:09:22")!!, itinerary5)
        val p2 = Positive(13, "positive12-2", df.parse("22/06/2021 13:09:22")!!, itinerary19)
        val p3 = Positive(14, "positive12-3", df.parse("23/06/2021 12:30:00")!!, itinerary20)

        // Mocks
        `when`(positiveAPI.getPositives(anyLong(), anyInt())).thenReturn(listOf(p1, p2, p3))

        riskContactVm.startChecking(now)

        verify(inAppNotificationManager).showRiskContactResultNotification(anyObject())
        verify(statisticsRepository).registerRiskContactResult(capture(checkResultCaptor))

        // Comprobar resultados almacenados en la base de datos
        val results = resultsVm.results.getOrAwaitValue()
        assertEquals(1, results.size)
        assertEquals(4, results[0].riskContacts.size)
        assertEquals(3, results[0].numberOfPositives)
        // Comprobar contactos de riesgo
        val rc1 = results[0].riskContacts[0]
        val rc2 = results[0].riskContacts[1]
        val rc3 = results[0].riskContacts[2]
        val rc4 = results[0].riskContacts[3]
        assertEquals(3, results[0].numberOfPositives)
        // Comprobar etiquetas de los contactos de riesgo
        assertEquals("Positivo 1", rc1.positiveLabel)
        assertEquals("Positivo 2", rc2.positiveLabel)
        assertEquals("Positivo 2", rc3.positiveLabel)
        assertEquals("Positivo 3", rc4.positiveLabel)

        // Contacto de riesgo 1
        // Puntos de contacto
        assertEquals(1L, rc1.riskContactId)
        assertEquals(3, rc1.contactLocations.size)
        assertEquals("10", rc1.contactLocations[0].userContactPointName)
        assertEquals("5", rc1.contactLocations[0].positiveContactPointName)
        assertEquals("11", rc1.contactLocations[1].userContactPointName)
        assertEquals("6", rc1.contactLocations[1].positiveContactPointName)
        assertEquals("12", rc1.contactLocations[2].userContactPointName)
        assertEquals("7", rc1.contactLocations[2].positiveContactPointName)
        // Fechas de inicio y de fin
        assertEquals("20/06/2021 16:45:40", df.format(rc1.startDate!!))
        assertEquals("20/06/2021 16:45:48", df.format(rc1.endDate!!))
        // Datos del contacto
        assertEquals(8000L, rc1.exposeTime)
        assertEquals(1.456, rc1.meanProximity, 0.001)
        assertEquals(5000L, rc1.meanTimeInterval)
        assertEquals(0.5048, rc1.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc1.riskLevel)

        // Contacto de riesgo 2
        // Puntos de contacto
        assertEquals(2L, rc2.riskContactId)
        assertEquals(4, rc2.contactLocations.size)
        assertEquals("10", rc2.contactLocations[0].userContactPointName)
        assertEquals("6", rc2.contactLocations[0].positiveContactPointName)
        assertEquals("11", rc2.contactLocations[1].userContactPointName)
        assertEquals("7", rc2.contactLocations[1].positiveContactPointName)
        assertEquals("12", rc2.contactLocations[2].userContactPointName)
        assertEquals("8", rc2.contactLocations[2].positiveContactPointName)
        assertEquals("13", rc2.contactLocations[3].userContactPointName)
        assertEquals("9", rc2.contactLocations[3].positiveContactPointName)
        // Fechas de inicio y de fin
        assertEquals("20/06/2021 16:45:38", df.format(rc2.startDate!!))
        assertEquals("20/06/2021 16:45:50", df.format(rc2.endDate!!))
        // Datos del contacto
        assertEquals(12000L, rc2.exposeTime)
        assertEquals(1.0106, rc2.meanProximity, 0.001)
        assertEquals(4833L, rc2.meanTimeInterval)
        assertEquals(0.5363, rc2.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc2.riskLevel)

        // Contacto de riesgo 3
        // Puntos de contacto
        assertEquals(3L, rc3.riskContactId)
        assertEquals(1, rc3.contactLocations.size)
        assertEquals("14", rc3.contactLocations[0].userContactPointName)
        assertEquals("10", rc3.contactLocations[0].positiveContactPointName)
        // Fechas de inicio y de fin
        assertNull(rc3.startDate)
        assertNull(rc3.endDate)
        // Datos del contacto
        assertEquals(0L, rc3.exposeTime)
        assertEquals(0.8879, rc3.meanProximity, 0.001)
        assertEquals(0L, rc3.meanTimeInterval)
        assertEquals(0.3408, rc3.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc3.riskLevel)

        // Contacto de riesgo 4
        // Puntos de contacto
        assertEquals(4L, rc4.riskContactId)
        assertEquals(3, rc4.contactLocations.size)
        assertEquals("15", rc4.contactLocations[0].userContactPointName)
        assertEquals("5", rc4.contactLocations[0].positiveContactPointName)
        assertEquals("16", rc4.contactLocations[1].userContactPointName)
        assertEquals("6", rc4.contactLocations[1].positiveContactPointName)
        assertEquals("17", rc4.contactLocations[2].userContactPointName)
        assertEquals("7", rc4.contactLocations[2].positiveContactPointName)
        // Fechas de inicio y de fin
        assertEquals("21/06/2021 09:21:28", df.format(rc4.startDate!!))
        assertEquals("21/06/2021 09:21:35", df.format(rc4.endDate!!))
        // Datos del contacto
        assertEquals(7000L, rc4.exposeTime)
        assertEquals(0.9124, rc4.meanProximity, 0.001)
        assertEquals(5000L, rc4.meanTimeInterval)
        assertEquals(0.5406, rc4.riskScore, 0.0001)
        assertEquals(RiskLevel.NARANJA, rc4.riskLevel)

        // Comprobar resultado que se envía a la nube
        assertEquals(48.0625, checkResultCaptor.value.riskPercent, 0.001)
        assertEquals(6750L, checkResultCaptor.value.exposeTime)
        assertEquals(1.066725, checkResultCaptor.value.proximity, 0.001)
    }

    /* Código: RCVM15 */
//    @Test
//    fun `error al obtener positivos de tiempo de espera agotado`() = testCoroutineRule.runBlockingTest {
//        // Mocks
//        `when`(positiveRepoSpy.getPositivesFromLastDays(3)).thenReturn(APIResult.NetworkError)
//
//        riskContactVm.startChecking(now)
//
//        // Verificar que se ha invocado al método para mostrar la notificación de error
//        verify(inAppNotificationManager).showRiskContactCheckErrorNotification()
//    }


    /**
     * Rellena la base de datos ROOM en memoria con los datos de prueba necesarios.
     */
    private fun fillDB() = testCoroutineRule.runBlockingTest {
        // Cargar itinearios
        itinerary15 = TestUtils.parseLocations("itinerario15.txt")
        itinerary16 = TestUtils.parseLocations("itinerario16.txt")
        itinerary19 = TestUtils.parseLocations("itinerario19.txt")
        itinerary2 = TestUtils.parseLocations("itinerario2.txt")
        itinerary5 = TestUtils.parseLocations("itinerario5.txt")
        itinerary1 = TestUtils.parseLocations("itinerario1.txt")
        itinerary14 = TestUtils.parseLocations("itinerario14.txt")
        itinerary20 = TestUtils.parseLocations("itinerario20.txt")


        // Rellenar base de datos local
        itinerary15.forEach {
            locationRepo.insertUserLocation(it)
        }
    }

    /**
     * Inicializa todos los mocks necesarios.
     */
    private fun initMocks() = testCoroutineRule.runBlockingTest {
        `when`(configRepository.getRiskContactConfig()).thenReturn(checkConfig)
    }
}

/* Código de ayuda para Mockito con Kotlin */
private fun <T> anyObject(): T {
    Mockito.anyObject<T>()
    return uninitialized()
}

private fun <T> uninitialized(): T = null as T

fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()