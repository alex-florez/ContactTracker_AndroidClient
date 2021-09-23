package es.uniovi.eii.contacttracker


import es.uniovi.eii.contacttracker.location.alarms.LocationAlarm
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

import java.util.Date

@RunWith(MockitoJUnitRunner::class)
class ExampleInstrumentedTest {

    @Mock
    private lateinit var locationRepository: LocationRepository

    @ExperimentalCoroutinesApi
    @Test
    fun test1() = runBlockingTest {
        val a = LocationAlarm(1, Date(), Date(), false)
        `when`(locationRepository.getAlarmByID(1)).thenReturn(a)

        val al = locationRepository.getAlarmByID(1)
        assertNotNull(al)
    }
}