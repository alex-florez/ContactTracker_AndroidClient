package es.uniovi.eii.contacttracker.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import es.uniovi.eii.contacttracker.model.Point
import es.uniovi.eii.contacttracker.model.UserLocation
import es.uniovi.eii.contacttracker.room.AppDatabase
import es.uniovi.eii.contacttracker.room.daos.UserLocationDao
import es.uniovi.eii.contacttracker.util.DateUtils
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

import java.util.Date

/**
 * Clase de pruebas unitarias para probar el funcionamiento de la
 * base de datos local ROOM de la aplicación.
 */
@RunWith(AndroidJUnit4::class)
class ContactTrackerDBTest {
    /* DAOs y base de datos de la App */
    private lateinit var userLocationDao: UserLocationDao
    private lateinit var db: AppDatabase

    /* Dispatchers para corrutinas */
    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)


    @Before
    fun createDB() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setTransactionExecutor(testDispatcher.asExecutor())
            .setQueryExecutor(testDispatcher.asExecutor())
            .build()
        userLocationDao = db.userLocationDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        db.close()
    }

    @Test
    fun testDB() = testScope.runBlockingTest {
        val date = DateUtils.toDate("18/09/2021 12:15", "dd/MM/yyyy HH:mm") ?: Date()
        val userLoc = UserLocation(
            null,
            Point(99.0, 99.0, date),
            20.0,
            "testing"
        )
        userLocationDao.insert(userLoc)
        val users = userLocationDao.getAllList()
        assertEquals(1, users.size)
    }
}