package es.uniovi.eii.contacttracker.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import es.uniovi.eii.contacttracker.getOrAwaitValue
import es.uniovi.eii.contacttracker.model.Point
import es.uniovi.eii.contacttracker.model.RiskContact
import es.uniovi.eii.contacttracker.model.RiskContactLocation
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.room.AppDatabase
import es.uniovi.eii.contacttracker.room.daos.LocationAlarmDao
import es.uniovi.eii.contacttracker.room.daos.RiskContactDao
import es.uniovi.eii.contacttracker.room.mappers.toResultWithRiskContacts
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.text.SimpleDateFormat

/**
 * Clase de pruebas Unitarias para el DAO de resultados de
 * comprobaciones y contactos de riesgo.
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RiskContactDaoTest {

    /* DAO y base de datos ROOM */
    private lateinit var db: AppDatabase
    private lateinit var riskContactDao: RiskContactDao

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
        riskContactDao = db.riskContactDao()
        // Rellenar la base de datos
        fillDB()
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        db.close()
    }

    /**
     * Inicializa la base de datos rellenándola con datos de prueba.
     */
    private fun fillDB() = runBlockingTest {
        // Localizaciones de contactos de riesgo
        val l1 = RiskContactLocation(1, "u1",
            Point(10.0, 20.0, df.parse("21/09/2021 12:00:00")!!),
            "p1", Point(12.0, 22.0, df.parse("21/09/2021 12:10:00")!!))
        val l2 = RiskContactLocation(2, "u2",
            Point(10.0, 20.0, df.parse("21/09/2021 12:00:00")!!),
            "p2", Point(12.0, 22.0, df.parse("21/09/2021 12:10:00")!!))
        val l3 = RiskContactLocation(3, "u3",
            Point(10.0, 20.0, df.parse("21/09/2021 12:00:00")!!),
            "p3", Point(12.0, 22.0, df.parse("21/09/2021 12:10:00")!!))
        val l4 = RiskContactLocation(4, "u4",
            Point(10.0, 20.0, df.parse("21/09/2021 12:00:00")!!),
            "p4", Point(12.0, 22.0, df.parse("21/09/2021 12:10:00")!!))
        // Contactos de riesgo
        val c1 = RiskContact(1, contactLocations = mutableListOf(l1, l2))
        val c2 = RiskContact(2, contactLocations = mutableListOf(l3, l4))
        // Resultados
        val r1 = RiskContactResult(1, mutableListOf(c1), 1, df.parse("21/09/2021 09:55:00")!!)
        val r2 = RiskContactResult(2, mutableListOf(c2), 1, df.parse("20/09/2021 19:34:00")!!)

        // Wrappers
        val wrapper1 = toResultWithRiskContacts(r1)
        val wrapper2 = toResultWithRiskContacts(r2)
        // Insertar resultados
        val id1 = riskContactDao.insert(wrapper1.riskContactResult)
        val id2 = riskContactDao.insert(wrapper2.riskContactResult)
        // Insertar contactos de riesgo
        wrapper1.riskContacts.forEach {
            it.riskContact.riskContactResultId = id1
            val rcID = riskContactDao.insertRiskContact(it.riskContact)
            // Insertar localizaciones de contacto
            it.riskContactLocations.forEach { rcLocation ->
                rcLocation.riskContactId = rcID
            }
            riskContactDao.insertRiskContactLocations(it.riskContactLocations)
        }
        wrapper2.riskContacts.forEach {
            it.riskContact.riskContactResultId = id2
            val rcID = riskContactDao.insertRiskContact(it.riskContact)
            // Insertar localizaciones de contacto
            it.riskContactLocations.forEach { rcLocation ->
                rcLocation.riskContactId = rcID
            }
            riskContactDao.insertRiskContactLocations(it.riskContactLocations)
        }
    }

    /* Código: RCDAO1 */
    @Test
    fun insert_risk_contact_result() = runBlockingTest {
        // Localizaciones
        val l1 = RiskContactLocation(null, "u5",
            Point(10.0, 20.0, df.parse("20/09/2021 14:00:00")!!),
            "p5", Point(12.0, 22.0, df.parse("20/09/2021 14:10:00")!!))
        val l2 = RiskContactLocation(null, "u6",
            Point(10.0, 20.0, df.parse("21/09/2021 11:00:00")!!),
            "p6", Point(12.0, 22.0, df.parse("21/09/2021 11:10:00")!!))
        // Contactos de riesgo
        val c = RiskContact(null, contactLocations = mutableListOf(l1, l2))
        // Resultado
        val r = RiskContactResult(null, mutableListOf(c), 1, df.parse("21/09/2021 13:45:00")!!)
        val wrapper = toResultWithRiskContacts(r)
        val id1 = riskContactDao.insert(r)
        wrapper.riskContacts.forEach {
            it.riskContact.riskContactResultId = id1
            val rcID = riskContactDao.insertRiskContact(it.riskContact)
            // Insertar localizaciones de contacto
            it.riskContactLocations.forEach { rcLocation ->
                rcLocation.riskContactId = rcID
            }
            riskContactDao.insertRiskContactLocations(it.riskContactLocations)
        }
        // Buscar por ID
        val result = riskContactDao.getResultById(3)
        assertNotNull(result)
        result?.let {
            assertEquals(3L, it.riskContactResult.resultId)
            assertEquals(1, it.riskContacts.size)
            assertEquals(3L, it.riskContacts[0].riskContact.riskContactId)
            assertEquals(5L, it.riskContacts[0].riskContactLocations[0].riskContactLocationId)
            assertEquals(6L, it.riskContacts[0].riskContactLocations[1].riskContactLocationId)
        }
    }

    /* Código: RCDAO2 */
    @Test
    fun get_result_by_id() {
        val result = riskContactDao.getResultById(2)
        assertNotNull(result)
        result?.let {
            assertEquals(2L, it.riskContactResult.resultId)
            assertEquals(1, it.riskContacts.size)
            assertEquals(2L, it.riskContacts[0].riskContact.riskContactId)
            assertEquals(3L, it.riskContacts[0].riskContactLocations[0].riskContactLocationId)
            assertEquals(4L, it.riskContacts[0].riskContactLocations[1].riskContactLocationId)
        }
    }

    /* Código: RCDAO3 */
    @Test
    fun get_all_results() {
        val results = riskContactDao.getAllRiskContactResults().getOrAwaitValue()
        assertEquals(2, results.size)

        assertEquals(1L, results[0].riskContactResult.resultId)
        assertEquals(1, results[0].riskContacts.size)
        assertEquals(1L, results[0].riskContacts[0].riskContact.riskContactId)
        assertEquals(1L, results[0].riskContacts[0].riskContactLocations[0].riskContactLocationId)
        assertEquals(2L, results[0].riskContacts[0].riskContactLocations[1].riskContactLocationId)

        assertEquals(2L, results[1].riskContactResult.resultId)
        assertEquals(1, results[1].riskContacts.size)
        assertEquals(2L, results[1].riskContacts[0].riskContact.riskContactId)
        assertEquals(3L, results[1].riskContacts[0].riskContactLocations[0].riskContactLocationId)
        assertEquals(4L, results[1].riskContacts[0].riskContactLocations[1].riskContactLocationId)
    }
}