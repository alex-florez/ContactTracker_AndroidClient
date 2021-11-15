package es.uniovi.eii.contacttracker.viewmodels

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.TestCoroutineRule
import es.uniovi.eii.contacttracker.fragments.dialogs.notifyquestions.ASYMPTOMATIC_QUESTION
import es.uniovi.eii.contacttracker.fragments.dialogs.notifyquestions.VACCINATED_QUESTION
import es.uniovi.eii.contacttracker.getOrAwaitValue
import es.uniovi.eii.contacttracker.model.*
import es.uniovi.eii.contacttracker.network.api.PositiveAPI
import es.uniovi.eii.contacttracker.network.model.NotifyPositiveResponse
import es.uniovi.eii.contacttracker.positive.PositiveManager
import es.uniovi.eii.contacttracker.repositories.ConfigRepository
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.repositories.PersonalDataRepository
import es.uniovi.eii.contacttracker.repositories.PositiveRepository
import es.uniovi.eii.contacttracker.room.AppDatabase
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Clase pruebas de Integración entre el ViewModel de notificación de positivos,
 * el manager de positivos y los repositorios correspondientes. Las llamadas a
 * la API REST se mockean con Mockito.
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, manifest = "src/main/AndroidManifest.xml")
@HiltAndroidTest
@ExperimentalCoroutinesApi
class NotifyPositiveViewModelTest {

    /* Reglas */
    @get:Rule
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule() // Para hacer funcionar los LiveData

    @get:Rule
    val testCoroutineRule = TestCoroutineRule() // Para utilizar el Scope de Test

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    /* ViewModel */
    private lateinit var vm: NotifyPositiveViewModel

    /* Mocks */
    @Mock
    lateinit var configRepository: ConfigRepository
    @Mock
    lateinit var personalDataRepository: PersonalDataRepository
    @Mock
    lateinit var positiveAPI: PositiveAPI

    /* Base de datos ROOM */
    private lateinit var db: AppDatabase

    /* Date Formatter */
    private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    /* Fecha que simula la fecha actual */
    private val now = df.parse("23/09/2021 10:33:00")!!

    /* Configuración de la notificación de positivos */
    private val notifyConfig = NotifyPositiveConfig()
    /* Respuestas a las preguntas */
    private val answers = mapOf(ASYMPTOMATIC_QUESTION to true, VACCINATED_QUESTION to false)
    /* Datos personales */
    private val personalData = PersonalData(
        "71987655H", "Alex", "Flórez",
        "695 82 28 79", "Avilés", "33400")
    /* Respuesta a la notificación con éxito */
    private val notifyResponse = NotifyPositiveResponse("AFsEwtu7SD", 10)
    /* Localizaciones a subir */
    private val locations = listOf(
        UserLocation(1,Point(86.0, 120.0, df.parse("19/09/2021 12:54:45")!!), 0.0, ""),
        UserLocation(2,Point(86.556, 120.12, df.parse("19/09/2021 12:54:50")!!), 0.0, ""),
        UserLocation(3,Point(82.23, 121.2, df.parse("19/09/2021 13:30:12")!!), 0.0, ""),
        UserLocation(4,Point(20.2, 100.45, df.parse("20/09/2021 11:10:11")!!), 0.0, ""),
        UserLocation(5,Point(21.22, 1000.67, df.parse("20/09/2021 11:10:20")!!), 0.0, ""),
        UserLocation(6,Point(86.02, 120.11, df.parse("20/09/2021 11:11:06")!!), 0.0, ""),
        UserLocation(7,Point(86.516, 120.112, df.parse("21/09/2021 15:00:52")!!), 0.0, ""),
        UserLocation(8,Point(82.232, 121.22, df.parse("21/09/2021 15:00:58")!!), 0.0, ""),
        UserLocation(9,Point(21.27, 101.435, df.parse("21/09/2021 15:01:10")!!), 0.0, ""),
        UserLocation(10,Point(21.222, 102.637, df.parse("21/09/2021 15:01:15")!!), 0.0, ""))

    /* Repositorios reales */
    private lateinit var positiveRepo: PositiveRepository
    private lateinit var locationRepo: LocationRepository

    @Before
    fun setUp(){
        // Contexto
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        // Base de datos ROOM en memoria
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries() // Para poder ejecutar consultas en el hilo principal.
            .build()
        // Construir repositorios
        positiveRepo = PositiveRepository(
            positiveAPI, // Mock
            db.positiveDao(),
            testCoroutineRule.testCoroutineDispatcher) // En memoria
        locationRepo = LocationRepository(db.userLocationDao(), db.locationAlarmDao())
        // Construir el manager de positivos
        val manager = PositiveManager(
            positiveRepo,
            locationRepo,
            configRepository // Mock
        )
        vm = NotifyPositiveViewModel(
            manager,
            personalDataRepository,
            configRepository,
            testCoroutineRule.testCoroutineDispatcher
        )
        fillDB()
        initMocks()
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        db.close()
    }

    /* Código: NPVM134 */
    @Test
    fun `notificar positivo sin datos personales, intentar notificar otro, actualizar fechas y notificar otro con datos personales`() = testCoroutineRule.runBlockingTest {
        // Notificar positivo sin datos personales.
        vm.notifyPositive(false, answers, now)
        val result = vm.notifySuccess.getOrAwaitValue()
        // Comprobar LiveData
        assertEquals(R.string.notifyPositiveResultText, result.first)
        assertEquals(10, result.second)
        // Comprobar positivos almacenados en local
        vm.getLocalPositives()
        val positives = vm.localPositives.getOrAwaitValue()
        assertEquals(1, positives.size)
        assertEquals(1L, positives[0].positiveID)
        assertEquals("AFsEwtu7SD", positives[0].positiveCode)
        assertEquals(7, positives[0].locations.size)
        assertEquals(4L, positives[0].locations[0].userlocationID)
        assertEquals(10L, positives[0].locations[6].userlocationID)

        // Intentar notificar un nuevo positivo
        vm.notifyPositive(false, answers, now)
        val result2 = vm.notifyError.getOrAwaitValue()
        assertEquals(Pair(R.string.errorNotifyLimitExceeded, 2), result2)

        // Actualizar fecha del positivo
        positives[0].timestamp = df.parse("18/09/2021 10:00:30")!!
        positiveRepo.updatePositive(positives[0])

        // Volver a notificar
        vm.notifyPositive(false, answers, now)
        val result3 = vm.notifySuccess.getOrAwaitValue()
        assertEquals(R.string.notifyPositiveResultText, result3.first)
        assertEquals(10, result3.second)

        // Comprobar los positivos
        vm.getLocalPositives()
        val positives2 = vm.localPositives.getOrAwaitValue()
        assertEquals(2, positives2.size)

    }

    /* Código: NPVM2 */
    @Test
    fun `notificar positivo sin existir localizaciones`() = testCoroutineRule.runBlockingTest {
        vm.notifyPositive(false, answers, Date())
        val result = vm.notifyError.getOrAwaitValue()
        assertEquals(Pair(R.string.errorNotifyNoLocations, null), result)

        // Comprobar positivos en local
        vm.getLocalPositives()
        val positives = vm.localPositives.getOrAwaitValue()
        assertEquals(0, positives.size)
    }

    /* Código: NPVM3 */
//    @Test
//    fun `notificar positivo con error de red`() = testCoroutineRule.runBlockingTest {
//        `when`(apiCall<NotifyPositiveResult>(Dispatchers.IO, anyObject())).thenReturn(APIResult.NetworkError)
//            vm.notifyPositive(false, answers, now)
//            val result = vm.notifyError.getOrAwaitValue()
//            assertEquals(R.string.network_error, result)
//    }

    /* Código: NPVM7 */
    @Test
    fun `cargar periodo de infectividad`() = testCoroutineRule.runBlockingTest {
        vm.loadInfectivityPeriod()
        val infectivityPeriod = vm.infectivityPeriod.getOrAwaitValue()
        assertEquals(3, infectivityPeriod)
    }

    /**
     * Rellena la base de datos ROOM en memoria con los datos de prueba necesarios.
     */
    private fun fillDB() = testCoroutineRule.runBlockingTest {
        // Insertar localizaciones del usuario.
        locations.forEach {
            locationRepo.insertUserLocation(it)
        }
    }

    /**
     * Inicializa todos los mocks necesarios.
     */
    private fun initMocks() = testCoroutineRule.runBlockingTest {
        `when`(configRepository.getNotifyPositiveConfig()).thenReturn(notifyConfig)
        `when`(positiveAPI.notifyPositive(anyObject())).thenReturn(notifyResponse)
    }
}

/* Código de ayuda para Mockito con Kotlin */
private fun <T> anyObject(): T {
    Mockito.anyObject<T>()
    return uninitialized()
}

private fun <T> uninitialized(): T = null as T