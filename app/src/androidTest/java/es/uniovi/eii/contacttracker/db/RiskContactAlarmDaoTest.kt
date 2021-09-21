package es.uniovi.eii.contacttracker.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.riskcontact.alarms.RiskContactAlarm
import es.uniovi.eii.contacttracker.room.AppDatabase
import es.uniovi.eii.contacttracker.room.daos.RiskContactAlarmDao
import es.uniovi.eii.contacttracker.room.daos.UserLocationDao
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
 * Clase de pruebas Unitarias para el DAO de alarmas de comprobación
 * de contactos de riesgo.
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RiskContactAlarmDaoTest {

    /* DAO y base de datos de la App */
    private lateinit var riskContactAlarmDao: RiskContactAlarmDao
    private lateinit var db: AppDatabase

    /* Dispatchers para corrutinas */
    private val testDispatcher = TestCoroutineDispatcher()

    /* Date formatter */
    private val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    @Before
    fun createDB() {
        val context = ApplicationProvider.getApplicationContext<Context>() // Obtener el contexto.
        // Crear la base de datos ROOM en memoria.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setTransactionExecutor(testDispatcher.asExecutor()) // Establecer dispatcher de test
            .setQueryExecutor(testDispatcher.asExecutor())
            .build()
        // Instanciar DAO
        riskContactAlarmDao = db.riskContactAlarmDao()
        // Rellenar la base de datos con datos
        fillDB()
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        db.close()
    }

    /**
     * Inicializa la base de datos rellenándola con los datos de prueba.
     */
    private fun fillDB() = runBlockingTest {
        val a1 = RiskContactAlarm(1L, df.parse("20/09/2021 13:00:00")!!, true)
        val a2 = RiskContactAlarm(2L, df.parse("20/09/2021 16:35:00")!!, true)
        riskContactAlarmDao.insert(a1)
        riskContactAlarmDao.insert(a2)
    }

    /* Código: RCADAO1 */
    @Test
    fun insert_risk_contact_alarm() = runBlockingTest {
        val newAlarm = RiskContactAlarm(null, df.parse("19/09/2021 14:36:00")!!, true)
        val id = riskContactAlarmDao.insert(newAlarm)
        assertEquals(3L, id)
        // Recuperar la alarma recién insertada
        val inserted = riskContactAlarmDao.getById(3)
        assertNotNull(inserted)
        inserted?.let {
            assertEquals(3L, it.id)
            assertEquals("19/09/2021 14:36:00", df.format(it.startDate))
            assertTrue(it.active)
        }
    }

    /* Código: RCADAO2 */
    @Test
    fun update_multiple_risk_contact_alarms() = runBlockingTest {
        val newAlarm1 = RiskContactAlarm(1, df.parse("23/09/2021 10:00:00")!!, false)
        val newAlarm2 = RiskContactAlarm(2, df.parse("22/09/2021 19:30:00")!!, false)
        // Comprobar estado previo
        var oldAlarm1 = riskContactAlarmDao.getById(1)
        var oldAlarm2 = riskContactAlarmDao.getById(2)
        assertNotNull(oldAlarm1)
        assertNotNull(oldAlarm2)
        oldAlarm1?.let {
            assertEquals(1L, it.id)
            assertEquals("20/09/2021 13:00:00", df.format(it.startDate))
            assertTrue(it.active)
        }
        oldAlarm2?.let {
            assertEquals(2L, it.id)
            assertEquals("20/09/2021 16:35:00", df.format(it.startDate))
            assertTrue(it.active)
        }
        // Actualizarlas
        riskContactAlarmDao.update(listOf(newAlarm1, newAlarm2))
        oldAlarm1 = riskContactAlarmDao.getById(1)
        oldAlarm2 = riskContactAlarmDao.getById(2)
        // Comprobar la actualización
        oldAlarm1?.let {
            assertEquals(1L, it.id)
            assertEquals("23/09/2021 10:00:00", df.format(it.startDate))
            assertFalse(it.active)
        }
        oldAlarm2?.let {
            assertEquals(2L, it.id)
            assertEquals("22/09/2021 19:30:00", df.format(it.startDate))
            assertFalse(it.active)
        }
    }

    /* Código: RCADAO3 */
    @Test
    fun delete_risk_contact_alarm_by_id() = runBlockingTest {
        var alarms = riskContactAlarmDao.getAll()
        assertEquals(2, alarms.size)
        // Eliminar la alarma 1
        riskContactAlarmDao.remove(1)
        assertNull(riskContactAlarmDao.getById(1))
        alarms = riskContactAlarmDao.getAll()
        assertEquals(1, alarms.size)
    }

    /* Código: RCADAO4 */
    @Test
    fun get_all_risk_contact_alarms() = runBlockingTest {
        val alarms = riskContactAlarmDao.getAll()
        assertEquals(2, alarms.size)
        assertEquals(1L, alarms[0].id)
        assertEquals("20/09/2021 13:00:00", df.format(alarms[0].startDate))
        assertTrue(alarms[0].active)
        assertEquals(2L, alarms[1].id)
        assertEquals("20/09/2021 16:35:00", df.format(alarms[1].startDate))
        assertTrue(alarms[1].active)
    }

    /* Código: RCADAO5 */
    @Test
    fun get_all_risk_contact_alarms_set_at_hour_no_match() = runBlockingTest {
        val hour = "12:00:00"
        val alarms = riskContactAlarmDao.getAlarmsBySetHour(hour)
        assertEquals(0, alarms.size)
    }

    /* Código: RCADAO6 */
    @Test
    fun get_all_risk_contact_alarm_set_at_hour_multiple_match() = runBlockingTest {
        // Insertar nueva alarma de prueba
        val newAlarm = RiskContactAlarm(null, df.parse("21/09/2021 13:00:00")!!, true)
        riskContactAlarmDao.insert(newAlarm)
        val hour = "13:00:00"
        val alarms = riskContactAlarmDao.getAlarmsBySetHour(hour)
        assertEquals(2, alarms.size)
        assertEquals(1L, alarms[0].id)
        assertEquals(3L, alarms[1].id)
    }
}