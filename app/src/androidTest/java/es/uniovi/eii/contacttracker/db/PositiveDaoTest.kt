package es.uniovi.eii.contacttracker.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import es.uniovi.eii.contacttracker.model.Point
import es.uniovi.eii.contacttracker.model.Positive
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.room.AppDatabase
import es.uniovi.eii.contacttracker.room.daos.PositiveDao
import es.uniovi.eii.contacttracker.room.daos.UserLocationDao
import es.uniovi.eii.contacttracker.room.relations.PositiveUserLocationCrossRef
import es.uniovi.eii.contacttracker.room.relations.PositiveWithLocations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.text.SimpleDateFormat

/**
 * Clase de pruebas Unitarias para el DAO de positivos.
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class PositiveDaoTest {

    /* DAO y base de datos ROOM */
    private lateinit var db: AppDatabase
    private lateinit var positiveDao: PositiveDao
    private lateinit var userLocationDao: UserLocationDao

    /* Dispatcher de Test */
    private val testDispatcher = TestCoroutineDispatcher()

    /* Date Formatter */
    private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    @Before
    fun createDB() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setTransactionExecutor(testDispatcher.asExecutor())
            .setQueryExecutor(testDispatcher.asExecutor())
            .build()
        positiveDao = db.positiveDao()
        userLocationDao = db.userLocationDao()
        // Rellenar la base de datos
        fillDB()
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        db.close()
    }

    /**
     * Inicializa la base de datos rellenándola con los datos iniciales.
     */
    private fun fillDB() = runBlockingTest {
        // Insertar localizaciones.
        val l1 = UserLocation(1, Point(10.0, 20.0, df.parse("18/09/2021 14:00:00")!!), 20.0, "testing")
        val l2 = UserLocation(2, Point(10.5, 20.5, df.parse("18/09/2021 14:15:30")!!), 20.0, "testing")
        val l3 = UserLocation(3, Point(11.5, 23.5, df.parse("17/09/2021 18:10:45")!!), 20.0, "testing")
        val l4 = UserLocation(4, Point(9.5, -23.5, df.parse("15/09/2021 17:56:05")!!), 20.0, "testing")
        val l5 = UserLocation(5, Point(11.34, -8.35, df.parse("17/09/2021 10:30:00")!!), 20.0, "testing")
        val l6 = UserLocation(6, Point(13.0, 12.3, df.parse("15/09/2021 09:55:08")!!), 20.0, "testing")

        userLocationDao.insert(l1)
        userLocationDao.insert(l2)
        userLocationDao.insert(l3)
        userLocationDao.insert(l4)
        userLocationDao.insert(l5)
        userLocationDao.insert(l6)

        // Insertar positivos y sus referencias cruzadas con las localizaciones.
        val p1 = Positive(1, "Positivo1", df.parse("20/09/2021 12:15:05")!!, listOf(l1, l2, l3))
        val p2 = Positive(2, "Positivo2", df.parse("20/09/2021 10:55:20")!!, listOf(l4, l5))
        val p3 = Positive(3, "Positivo3", df.parse("21/09/2021 17:46:00")!!, listOf(l6))
        val positives = listOf(p1, p2, p3)
        positives.forEach { p ->
            val id = positiveDao.insert(p)
            p.locations.forEach {
                if(it.userlocationID != null){
                    val crossRef = PositiveUserLocationCrossRef(id, it.userlocationID!!)
                    positiveDao.insert(crossRef)
                }
            }
        }
    }

    /* Código: PDAO1 */
    @Test
    fun insert_positive_with_locations() = runBlockingTest {
        // Localizaciones
        val l1 = UserLocation(7, Point(10.0, 20.0, df.parse("19/09/2021 14:00:00")!!), 20.0, "testing")
        val l2 = UserLocation(8, Point(10.5, 20.5, df.parse("19/09/2021 14:15:30")!!), 20.0, "testing")
        val locations = listOf(l1, l2)
        locations.forEach {
            userLocationDao.insert(it)
        }
        // Positivo
        val newPositive = Positive(4, "Positivo4", df.parse("20/09/2021 11:57:00")!!, listOf(l1, l2))
        val positiveID = positiveDao.insert(newPositive)
        newPositive.locations.forEach {
            if(it.userlocationID != null){
                val crossRef = PositiveUserLocationCrossRef(positiveID, it.userlocationID!!)
                positiveDao.insert(crossRef)
            }
        }
        val inserted = positiveDao.getPositiveByID(4)
        assertNotNull(inserted)
        assertEquals(4L, inserted.positive.positiveID)
        assertEquals("Positivo4", inserted.positive.positiveCode)
        assertEquals("20/09/2021 11:57:00", df.format(inserted.positive.timestamp))
        assertEquals(2, inserted.locations.size)
        assertEquals(7L, inserted.locations[0].userlocationID)
        assertEquals(8L, inserted.locations[1].userlocationID)
    }

    /* Código: PDAO2 */
    @Test
    fun get_all_positives_with_locations() = runBlockingTest {
        val positives = positiveDao.getAllPositives()
        // Comprobar IDs de los positivos
        assertEquals(3, positives.size)
        assertEquals(3L, positives[0].positive.positiveID)
        assertEquals(1L, positives[1].positive.positiveID)
        assertEquals(2L, positives[2].positive.positiveID)
        // Comprobar localizaciones
        val p3Locations = positives[0].locations
        val p1Locations = positives[1].locations
        val p2Locations = positives[2].locations
        assertEquals(6L, p3Locations[0].userlocationID)
        assertEquals(4L, p2Locations[0].userlocationID)
        assertEquals(5L, p2Locations[1].userlocationID)
        assertEquals(1L, p1Locations[0].userlocationID)
        assertEquals(2L, p1Locations[1].userlocationID)
        assertEquals(3L, p1Locations[2].userlocationID)
    }

    /* Código: PDAO3 */
    @Test
    fun get_all_positives_codes() = runBlockingTest {
        val codes = positiveDao.getAllPositiveCodes()
        assertEquals(3, codes.size)
        assertEquals("Positivo1", codes[0])
        assertEquals("Positivo2", codes[1])
        assertEquals("Positivo3", codes[2])
    }

    /* Código: PDAO4 */
    @Test
    fun get_positives_notified_on_date_no_match() = runBlockingTest {
        val date = "2021-09-19"
        val n = positiveDao.getNumberOfNotifiedPositivesAt(date)
        assertEquals(0, n)
    }

    /* Código: PDAO5 */
    @Test
    fun get_positives_notified_on_date_match() = runBlockingTest {
        val date = "2021-09-20"
        val n = positiveDao.getNumberOfNotifiedPositivesAt(date)
        assertEquals(2, n)
    }

    /* Código: PDAO6 */
    @Test
    fun get_last_notified_positive() = runBlockingTest {
        val last = positiveDao.getLastNotifiedPositive()
        assertNotNull(last)
        assertEquals(3L, last?.positiveID)
        assertEquals("Positivo3", last?.positiveCode)
    }

    /* Código: PDAO7 */
    @Test
    fun get_last_notified_positive_with_empty_positives() = runBlockingTest {
        db.clearAllTables()
        val last = positiveDao.getLastNotifiedPositive()
        assertNull(last)
    }
}