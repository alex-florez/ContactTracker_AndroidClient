package es.uniovi.eii.contacttracker.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import es.uniovi.eii.contacttracker.getOrAwaitValueAndroid
import es.uniovi.eii.contacttracker.model.Point
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.room.AppDatabase
import es.uniovi.eii.contacttracker.room.daos.UserLocationDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.io.IOException
import java.text.SimpleDateFormat

/**
 * Clase de pruebas Unitarias para probar el funcionamiento del DAO
 * de localizaciones de usuario.
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class UserLocationDaoTest {
    /* DAO y base de datos de la App */
    private lateinit var userLocationDao: UserLocationDao
    private lateinit var db: AppDatabase

    /* Dispatchers para corrutinas */
    private val testDispatcher = TestCoroutineDispatcher()

    /* Date formatter */
    private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    /* Lista inicial de localizaciones */
    private var locations = mutableListOf<UserLocation>()

    /* Regla para que funcionen los LiveData */
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDB() {
        val context = ApplicationProvider.getApplicationContext<Context>() // Obtener el contexto.
        // Crear la base de datos ROOM en memoria.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setTransactionExecutor(testDispatcher.asExecutor()) // Establecer dispatcher de test
            .setQueryExecutor(testDispatcher.asExecutor())
            .build()
        // Instanciar DAO
        userLocationDao = db.userLocationDao()
        // Rellenar la base de datos con datos
        fillDB()
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        db.close()
    }

    /**
     * Rellena la base de datos con los datos necesarios
     * para cubrir las situaciones de prueba.
     */
    private fun fillDB() = runBlockingTest {
        val l1 = UserLocation(1, Point(10.0, 20.0, df.parse("19/09/2021 14:00:00")!!), 20.0, "testing")
        val l2 = UserLocation(2, Point(10.5, 20.5, df.parse("19/09/2021 14:15:30")!!), 20.0, "testing")
        val l3 = UserLocation(3, Point(11.5, 23.5, df.parse("19/09/2021 18:10:45")!!), 20.0, "testing")
        val l4 = UserLocation(4, Point(9.5, -23.5, df.parse("15/09/2021 17:56:05")!!), 20.0, "testing")
        val l5 = UserLocation(5, Point(11.34, -8.35, df.parse("17/09/2021 10:30:00")!!), 20.0, "testing")
        val l6 = UserLocation(6, Point(13.0, 12.3, df.parse("22/09/2021 09:55:08")!!), 20.0, "testing")

        locations.add(l1)
        locations.add(l2)
        locations.add(l3)
        locations.add(l4)
        locations.add(l5)
        locations.add(l6)

        locations.forEach {
            userLocationDao.insert(it)
        }
    }

    /* Código: ULDAO1 */
    @Test
    fun add_new_user_location() = runBlockingTest {
        val newUserLoc = UserLocation(null, Point(11.0, -22.0, df.parse("20/09/2021 13:45:05")!!), 0.0, "testing")
        val id = userLocationDao.insert(newUserLoc)
        val locations = userLocationDao.getAllList()

        assertEquals(7, locations.size) // Comprobar tamaño
        assertEquals(7L, id) // Comprobar ID
        val insertedLocation = userLocationDao.getByID(7)
        // Comprobar datos almacenados
        assertNotNull(insertedLocation)
        insertedLocation?.let {
            assertEquals(11.0, it.lat(), 0.01)
            assertEquals(-22.0, it.lng(), 0.01)
            assertEquals("20/09/2021 13:45:05", df.format(it.timestamp()))
            assertEquals(0.0, it.accuracy, 0.01)
            assertEquals("testing", it.provider)
        }
    }

    /* Código: ULDAO2 */
    @Test
    fun get_all_user_locations_list() {
        val all = userLocationDao.getAllList()
        assertEquals(6, all.size)
        // Comprobar el orden
        assertEquals(6L, all[0].userlocationID)
        assertEquals(4L, all[all.size-1].userlocationID)
    }

    /* Código: ULDAO3 */
    @Test
    fun get_user_location_by_id() = runBlockingTest {
        // Localización no existente
        assertNull(userLocationDao.getByID(99))
        // Localizción existente
        val location = userLocationDao.getByID(5)
        assertNotNull(location)
        location?.let {
           assertEquals(it, locations[4])
        }
    }

    /* Código: ULDAO4 */
    @Test
    fun get_user_locations_by_date_no_match() {
        val dateString = "2021-12-10"
        val locations = userLocationDao.getAllByDateString(dateString)
        assertEquals(0, locations.getOrAwaitValueAndroid().size)
    }

    /* Código: ULDAO5 */
    @Test
    fun get_user_locations_by_date_match() {
        val dateString = "2021-09-19"
        val filtered = userLocationDao.getAllByDateString(dateString)
        assertEquals(3, filtered.getOrAwaitValueAndroid().size)
        assertEquals(locations[2], filtered.getOrAwaitValueAndroid()[0])
        assertEquals(locations[1], filtered.getOrAwaitValueAndroid()[1])
        assertEquals(locations[0], filtered.getOrAwaitValueAndroid()[2])
    }

    /* Código: ULDAO6 */
    @Test
    fun delete_all_user_locations() = runBlockingTest {
        val locations = userLocationDao.getAllList()
        assertEquals(6, locations.size)
        val deleted = userLocationDao.deleteAll()
        assertEquals(6, deleted)
        assertEquals(0, userLocationDao.getAllList().size)
    }

    /* Código: ULDAO7 */
    @Test
    fun delete_user_locations_by_date_no_match() = runBlockingTest {
        val locations = userLocationDao.getAllList()
        assertEquals(6, locations.size)
        val stringDate = "2021-12-10"
        val deleted = userLocationDao.deleteByDate(stringDate)
        assertEquals(0, deleted)
        assertEquals(6, userLocationDao.getAllList().size)
    }

    /* Código: ULDAO8 */
    @Test
    fun delete_user_locations_by_date_match() = runBlockingTest {
        val locations = userLocationDao.getAllList()
        assertEquals(6, locations.size)
        val stringDate = "2021-09-19"
        val deleted = userLocationDao.deleteByDate(stringDate)
        assertEquals(3, deleted)
        assertEquals(3, userLocationDao.getAllList().size)
    }

    /* Código: ULDAO9 */
    @Test
    fun get_locations_between_dates_no_match_right() = runBlockingTest {
        val start = "2021-09-23"
        val end = "2021-09-25"
        val locations = userLocationDao.getLocationsBetween(start, end)
        assertEquals(0, locations.size)
    }

    /* Código: ULDAO10 */
    @Test
    fun get_locations_between_dates_no_match_left() = runBlockingTest {
        val start = "2021-09-11"
        val end = "2021-09-14"
        val locations = userLocationDao.getLocationsBetween(start, end)
        assertEquals(0, locations.size)
    }

    /* Código: ULDAO11 */
    @Test
    fun get_locations_between_dates_left_match() = runBlockingTest {
        val start = "2021-09-19"
        val end = "2021-09-20"
        val locations = userLocationDao.getLocationsBetween(start, end)
        assertEquals(3, locations.size)
        assertEquals(3L, locations[0].userlocationID)
        assertEquals(2L, locations[1].userlocationID)
        assertEquals(1L, locations[2].userlocationID)
    }

    /* Código: ULDAO12 */
    @Test
    fun get_locations_between_dates_inner_match() = runBlockingTest {
        val start = "2021-09-16"
        val end = "2021-09-20"
        val locations = userLocationDao.getLocationsBetween(start, end)
        assertEquals(4, locations.size)
        assertEquals(3L, locations[0].userlocationID)
        assertEquals(2L, locations[1].userlocationID)
        assertEquals(1L, locations[2].userlocationID)
        assertEquals(5L, locations[3].userlocationID)
    }

    /* Código: ULDAO13 */
    @Test
    fun get_locations_between_dates_right_match() = runBlockingTest {
        val start = "2021-09-20"
        val end = "2021-09-22"
        val locations = userLocationDao.getLocationsBetween(start, end)
        assertEquals(1, locations.size)
        assertEquals(6L, locations[0].userlocationID)
    }

    /* Código: ULDAO14 */
    @Test
    fun get_locations_between_dates_multiple_match() = runBlockingTest {
        val start = "2021-09-14"
        val end = "2021-09-17"
        val locations = userLocationDao.getLocationsBetween(start, end)
        assertEquals(2, locations.size)
        assertEquals(5L, locations[0].userlocationID)
        assertEquals(4L, locations[1].userlocationID)
    }
}