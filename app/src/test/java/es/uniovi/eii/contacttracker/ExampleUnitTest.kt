package es.uniovi.eii.contacttracker

import es.uniovi.eii.contacttracker.model.Itinerary
import es.uniovi.eii.contacttracker.util.TestUtils
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private var itinerary1: Itinerary = Itinerary(mapOf())

    @Before
    fun init(){
      itinerary1 = TestUtils.parseItinerary("itinerary1.txt")
    }

    @Test
    fun addition_isCorrect() {
        assertEquals("2", "2")
        print(itinerary1.locations.toString())
    }
}