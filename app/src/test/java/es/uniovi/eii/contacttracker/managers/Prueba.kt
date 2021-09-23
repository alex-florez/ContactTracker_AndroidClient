package es.uniovi.eii.contacttracker.managers

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import es.uniovi.eii.contacttracker.model.NotifyPositiveConfig
import es.uniovi.eii.contacttracker.positive.PositiveManager
import es.uniovi.eii.contacttracker.repositories.ConfigRepository
import es.uniovi.eii.contacttracker.repositories.LocationRepository
import es.uniovi.eii.contacttracker.repositories.PositiveRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import javax.inject.Inject

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class Prueba {

    @Mock
    lateinit var configRepository: ConfigRepository

    @Mock
    lateinit var positiveRepository: PositiveRepository

    @Mock
    lateinit var locationRepository: LocationRepository

    private lateinit var positiveManager: PositiveManager

    @Before
    fun setUp() = runBlockingTest {
        `when`(configRepository.getNotifyPositiveConfig()).thenReturn(
            NotifyPositiveConfig(5, 8)
        )

        positiveManager = PositiveManager(positiveRepository, locationRepository, configRepository)
    }

    @Test
    fun test1() = runBlockingTest {
        assertEquals(5, positiveManager.pruebaGet())
    }

}