package es.uniovi.eii.contacttracker.db

import android.content.Context
import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import es.uniovi.eii.contacttracker.getOrAwaitValue
import es.uniovi.eii.contacttracker.location.alarms.LocationAlarm
import es.uniovi.eii.contacttracker.room.AppDatabase
import es.uniovi.eii.contacttracker.room.daos.LocationAlarmDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.text.SimpleDateFormat

/**
 * Clase de pruebas Unitarias para el DAO de las alarmas de localizción.
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class LocationAlarmDaoTest {
    /* DAO y base de datos ROOM */
    private lateinit var db: AppDatabase
    private lateinit var locationAlarmDao: LocationAlarmDao

    /* Dispatcher de Test */
    private val testDispatcher = TestCoroutineDispatcher()

    /* Regla para los LiveData */
    @get:Rule
    var instantExecutor = InstantTaskExecutorRule()

    /* Date Formatter */
    private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    @Before
    fun createDB() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setTransactionExecutor(testDispatcher.asExecutor())
            .setQueryExecutor(testDispatcher.asExecutor())
            .build()
        locationAlarmDao = db.locationAlarmDao()
        // Rellenar la base de datos
        fillDB()
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        db.close()
    }

    /**
     * Inicializa la base de datos con los datos de prueba.
     */
    private fun fillDB() = runBlockingTest {
        val a1 = LocationAlarm(1, df.parse("20/09/2021 12:00:00")!!, df.parse("20/09/2021 13:00:00")!!, true)
        val a2 = LocationAlarm(2, df.parse("19/09/2021 16:30:00")!!, df.parse("19/09/2021 17:00:00")!!, true)
        // Fechas de creación
        a1.creationDate = df.parse("20/09/2021 11:32:45")!!
        a2.creationDate = df.parse("19/09/2021 13:40:00")!!
        locationAlarmDao.insert(a1)
        locationAlarmDao.insert(a2)
    }

    /* Código: LADAO1 */
    @Test
    fun insert_location_alarm() = runBlockingTest {
        val newAlarm = LocationAlarm(null, df.parse("20/09/2021 18:00:00")!!, df.parse("20/09/2021 18:25:00")!!, true)
        val id = locationAlarmDao.insert(newAlarm)
        assertEquals(3L, id)
        val alarm = locationAlarmDao.getByID(id)
        assertNotNull(alarm)
        assertEquals(3L, alarm?.id)
        assertEquals("20/09/2021 18:00:00", df.format(alarm?.startDate!!))
        assertEquals("20/09/2021 18:25:00", df.format(alarm.endDate))
        assertTrue(alarm.active)
    }

    /* Código: LADAO2 */
    @Test
    fun update_location_alarm() = runBlockingTest {
        val newAlarm = LocationAlarm(1, df.parse("25/09/2021 11:20:00")!!, df.parse("25/09/2021 11:32:00")!!, false)
        val updatedRows = locationAlarmDao.update(newAlarm)
        assertEquals(1, updatedRows)
        val updatedAlarm = locationAlarmDao.getByID(1)
        assertNotNull(updatedAlarm)
        assertEquals(1L, updatedAlarm?.id)
        assertEquals("25/09/2021 11:20:00", df.format(updatedAlarm?.startDate!!))
        assertEquals("25/09/2021 11:32:00", df.format(updatedAlarm.endDate))
        assertFalse(updatedAlarm.active)
    }

    /* Código: LADAO3 */
    @Test
    fun get_all_location_alarms() = runBlockingTest {
        val alarms = locationAlarmDao.getAll().getOrAwaitValue()
        assertEquals(2, alarms.size)
        assertEquals(1L, alarms[0].id)
        assertEquals("20/09/2021 12:00:00", df.format(alarms[0].startDate))
        assertEquals("20/09/2021 13:00:00", df.format(alarms[0].endDate))
        assertTrue(alarms[0].active)
        assertEquals(2L, alarms[1].id)
        assertEquals("19/09/2021 16:30:00", df.format(alarms[1].startDate))
        assertEquals("19/09/2021 17:00:00", df.format(alarms[1].endDate))
        assertTrue(alarms[1].active)
    }

    /* Código: LADAO4 */
    @Test
    fun get_alarm_by_id() = runBlockingTest {
        val alarm = locationAlarmDao.getByID(2)
        assertNotNull(alarm)
        alarm?.let {
            assertEquals(2L,it.id)
            assertEquals("19/09/2021 16:30:00", df.format(it.startDate))
            assertEquals("19/09/2021 17:00:00", df.format(it.endDate))
            assertTrue(it.active)
        }
    }

    /* Código: LADAO5 */
    @Test
    fun enable_and_disable_location_alarm() = runBlockingTest {
        var a1 = locationAlarmDao.getByID(1)!!
        assertTrue(a1.active)
        // Deshabilitar alarma 1
        locationAlarmDao.toggleState(1, false)
        a1 = locationAlarmDao.getByID(1)!!
        assertFalse(a1.active)
        // Habilitar alarma 1
        locationAlarmDao.toggleState(1, true)
        a1 = locationAlarmDao.getByID(1)!!
        assertTrue(a1.active)
    }

    /* Código: LADAO6 */
    @Test
    fun delete_all_location_alarms() = runBlockingTest {
        var alarms = locationAlarmDao.getAll().getOrAwaitValue()
        assertEquals(2, alarms.size)
        locationAlarmDao.deleteAll()
        alarms = locationAlarmDao.getAll().getOrAwaitValue()
        assertEquals(0, alarms.size)
    }

    /* Código: LADAO7 */
    @Test
    fun delete_alarm_by_id() = runBlockingTest {
        val deletedRows = locationAlarmDao.deleteById(1)
        assertEquals(1, deletedRows)
        val alarm = locationAlarmDao.getByID(1)
        assertNull(alarm)
    }

    /* Código: LADAO8 */
    @Test
    fun get_collisions_with_exact_match() = runBlockingTest {
        val start = "2021-09-20 12:00:00"
        val end = "2021-09-20 13:00:00"
        val collisions = locationAlarmDao.getCollisions(start, end)
        assertEquals(1, collisions.size)
        assertEquals(1L, collisions[0].id)
    }

    /* Código: LADAO9 */
    @Test
    fun get_collisions_with_inner_match() = runBlockingTest {
        val start = "2021-09-20 12:20:00"
        val end = "2021-09-20 12:30:00"
        val collisions = locationAlarmDao.getCollisions(start, end)
        assertEquals(1, collisions.size)
        assertEquals(1L, collisions[0].id)
    }

    /* Código: LADAO10 */
    @Test
    fun get_collisions_with_outer_match() = runBlockingTest {
        val start = "2021-09-20 11:30:00"
        val end = "2021-09-20 13:30:00"
        val collisions = locationAlarmDao.getCollisions(start, end)
        assertEquals(1, collisions.size)
        assertEquals(1L, collisions[0].id)
    }

    /* Código: LADAO11 */
    @Test
    fun get_collisions_with_left_match() = runBlockingTest {
        val start = "2021-09-20 11:30:00"
        val end = "2021-09-20 12:30:00"
        val collisions = locationAlarmDao.getCollisions(start, end)
        assertEquals(1, collisions.size)
        assertEquals(1L, collisions[0].id)
    }

    /* Código: LADAO12 */
    @Test
    fun get_collisions_with_right_match() = runBlockingTest {
        val start = "2021-09-20 12:30:00"
        val end = "2021-09-20 13:30:00"
        val collisions = locationAlarmDao.getCollisions(start, end)
        assertEquals(1, collisions.size)
        assertEquals(1L, collisions[0].id)
    }

    /* Código: LADAO13 */
    @Test
    fun get_collisions_with_multiple_match() = runBlockingTest {
        // Agregar una nueva alarma de prueba
        val newAlarm = LocationAlarm(3, df.parse("20/09/2021 13:10:00")!!, df.parse("20/09/2021 13:30:00")!!, true)
        locationAlarmDao.insert(newAlarm)
        val start = "2021-09-20 12:30:00"
        val end = "2021-09-20 13:30:00"
        val collisions = locationAlarmDao.getCollisions(start, end)
        assertEquals(2, collisions.size)
        assertEquals(1L, collisions[0].id)
        assertEquals(3L, collisions[1].id)
    }
}