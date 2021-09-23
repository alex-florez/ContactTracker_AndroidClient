package es.uniovi.eii.contacttracker.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import es.uniovi.eii.contacttracker.R
import es.uniovi.eii.contacttracker.fragments.riskcontacts.CheckMode
import es.uniovi.eii.contacttracker.getOrAwaitValue
import es.uniovi.eii.contacttracker.model.RiskContactResult
import es.uniovi.eii.contacttracker.repositories.RiskContactRepository
import es.uniovi.eii.contacttracker.riskcontact.RiskContactManager
import es.uniovi.eii.contacttracker.room.daos.RiskContactAlarmDao
import es.uniovi.eii.contacttracker.room.daos.RiskContactDao
import es.uniovi.eii.contacttracker.room.relations.ResultWithRiskContacts
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, manifest = "src/main/AndroidManifest.xml")
@HiltAndroidTest
class VMTest {


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var riskContactDao: RiskContactDao

    @Mock
    lateinit var riskContactAlarmDao: RiskContactAlarmDao

    private lateinit var vm: RiskContactResultViewModel

    private lateinit var repo: RiskContactRepository

    @Before
    fun setUp() {
        // Mock Shared Prefs
        val sharedPrefs = mock(SharedPreferences::class.java)
        // Mockear string
        `when`(sharedPrefs.getString(anyString(), anyString())).thenReturn("PERIODIC")

        val ctx = ApplicationProvider.getApplicationContext<Context>()
        repo = RiskContactRepository(
            riskContactDao,
            riskContactAlarmDao,
            sharedPrefs,
            ctx
        )
        vm = RiskContactResultViewModel(repo)
        val rc = RiskContactResult(1, mutableListOf())
        val r = ResultWithRiskContacts(rc, listOf())
        `when`(riskContactDao.getAllRiskContactResults()).thenReturn(
            MutableLiveData(listOf(r))
        )

    }

    @Test
    fun test1() {
        val results = vm.getAllRiskContactResults().getOrAwaitValue()
        assertEquals(1, results.size)


        val mode = repo.getCheckMode()
        assertEquals(CheckMode.PERIODIC, mode)

    }
}